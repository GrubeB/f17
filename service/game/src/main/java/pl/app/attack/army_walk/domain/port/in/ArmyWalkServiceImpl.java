package pl.app.attack.army_walk.domain.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.application.ArmyWalkType;
import pl.app.attack.army_walk.domain.application.Attack;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.item.inventory.application.port.in.InventoryCommand;
import pl.app.item.inventory.application.port.in.InventoryService;
import pl.app.item.item.application.domain.OfficerItem;
import pl.app.item.item.application.domain.OfficerType;
import pl.app.item.item.application.domain.Officers;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;
import pl.app.unit.unit.application.port.in.UnitDomainRepository;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import pl.app.unit.village_army.query.dto.VillageArmyDto;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import pl.app.village.village.query.VillageDtoQueryService;
import pl.app.village.village.query.dto.VillageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
class ArmyWalkServiceImpl implements ArmyWalkService {
    private static final Logger logger = LoggerFactory.getLogger(ArmyWalkServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final ArmyWalkDomainRepositoryImpl armyWalkDomainRepository;

    private final UnitDomainRepository unitDomainRepository;
    private final VillageDtoQueryService villageDtoQueryService;

    private final VillageService villageService;
    private final VillageArmyService villageArmyService;
    private final VillageInfrastructureService villageInfrastructureService;
    private final VillageResourceService villageResourceService;
    private final InventoryService inventoryService;


    @Override
    public Mono<ArmyWalk> process(ArmyWalkCommand.ProcessArmyArrivalCommand command) {
        return armyWalkDomainRepository.fetchById(command.getWalkId())
                .flatMap(armyWalk ->
                        switch (armyWalk.getType()) {
                            case ATTACK -> processAttack(armyWalk);
                            case RETURN -> processReturn(armyWalk);
                            case SUPPORT -> throw new RuntimeException("not implemented yet");
                            case RELOCATE -> throw new RuntimeException("not implemented yet");
                        }
                );

    }

    Mono<ArmyWalk> processReturn(ArmyWalk domain) {
        return Mono.fromCallable(() -> {
            domain.markAsProcessed();
            // unblock army, add resources
            return villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(domain.getTo().getVillageId(), domain.getArmy()))
                    .then(villageResourceService.add(new VillageResourceCommand.AddResourceCommand(domain.getTo().getVillageId(), domain.getResource())))
                    .then(mongoTemplate.save(domain));
        }).doOnSubscribe(subscription ->
                logger.debug("finishing army return: {}", domain.getArmyWalkId())
        ).flatMap(Function.identity()).doOnSuccess(d ->
                logger.debug("finishing army return: {}", d.getArmyWalkId())
        ).doOnError(e ->
                logger.error("exception occurred while finishing army return: {}, exception: {}", domain.getArmyWalkId(), e.toString())
        );
    }

    Mono<ArmyWalk> processAttack(ArmyWalk domain) {
        return Mono.fromCallable(() ->
                Mono.zip(
                                unitDomainRepository.fetchAll(),
                                villageDtoQueryService.fetchById(domain.getFrom().getVillageId()),
                                villageDtoQueryService.fetchById(domain.getTo().getVillageId())
                        )
                        .flatMap(t -> {
                            var units = t.getT1();
                            var attackerVillage = t.getT2();
                            var defenderVillage = t.getT3();
                            domain.markAsProcessed();

                            boolean attackerFaithBonus = attackerVillage.getVillageInfrastructure().getBuildings().meetRequirements(BuildingType.CHURCH, 1)
                                    || attackerVillage.getVillageInfrastructure().getBuildings().meetRequirements(BuildingType.CHAPEL, 1);
                            boolean defenderFaithBonus = defenderVillage.getVillageInfrastructure().getBuildings().meetRequirements(BuildingType.CHURCH, 1)
                                    || defenderVillage.getVillageInfrastructure().getBuildings().meetRequirements(BuildingType.CHAPEL, 1);

                            Army defenderArmy = Army.of(defenderVillage.getVillageArmy().getVillageArmy());
                            defenderArmy.add(defenderVillage.getVillageArmy().getSupportArmy());

                            var attack = new Attack(domain, defenderArmy, units,
                                    new Attack.DefenderVillage(defenderVillage.getVillageResource().getResource(), defenderVillage.getVillageInfrastructure().getBuildings().getWall().getLevel(), defenderVillage.getLoyalty()),
                                    attackerFaithBonus, defenderFaithBonus
                            );
                            if (checkIfRangerPreventAttack(domain, attack)) {
                                return retreatArmy(domain, units);
                            }
                            return mongoTemplate.save(domain)
                                    .then(mongoTemplate.insert(attack))
                                    .then(subtractAttackerUnits(attack))
                                    .then(subtractDefenderUnits(attack, defenderVillage))
                                    .then(destroyDefenderVillageWall(attack))
                                    .then(Mono.just(attack).filter(a -> a.getBattleResult().isAttackerWin())
                                            .flatMap(a -> attack.getVillageConquered() ?
                                                    conquerVillage(attack) : plunderVillage(attack).then(subtractLoyalty(attack)))
                                    )
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("finishing army attack: {}", domain.getArmyWalkId())
        ).flatMap(Function.identity()).doOnSuccess(d ->
                logger.debug("finished army attack: {}", d.getArmyWalkId())
        ).doOnError(e ->
                logger.error("exception occurred while finishing army attack: {}, exception: {}", domain.getArmyWalkId(), e.toString())
        );
    }

    private Mono<Void> subtractLoyalty(Attack attack) {
        if (attack.getLoyaltyDecrease() < 0) {
            return Mono.empty();
        }
        return villageService.subtractLoyalty(new VillageCommand.SubtractLoyaltyCommand(attack.getDefenderVillageId(), attack.getLoyaltyDecrease()))
                .then();
    }

    private Mono<ArmyWalk> retreatArmy(ArmyWalk domain, Map<UnitType, Unit> units) {
        var returnArmyWalk = new ArmyWalk(ArmyWalkType.RETURN, units,
                domain.getTo(),
                domain.getFrom(),
                domain.getArmy(), Resource.zero(),
                new Officers()
        );
        return mongoTemplate.save(returnArmyWalk)
                .then(mongoTemplate.save(domain));
    }

    private boolean checkIfRangerPreventAttack(ArmyWalk domain, Attack attack) {
        return domain.getOfficers().getRanger() && !attack.getBattleResult().isAttackerWin();
    }

    private Mono<Void> plunderVillage(Attack attack) {
        return villageResourceService.subtract(new VillageResourceCommand.SubtractResourceCommand(attack.getDefenderVillageId(), attack.getPlunderedResource()))
                .then(mongoTemplate.save(attack.getReturnArmyWalk()))
                .then();
    }

    private Mono<Void> conquerVillage(Attack attack) {
        return villageService.conquerVillage(new VillageCommand.ConquerVillageCommand(attack.getDefenderVillageId(), attack.getAttackerId()))
                .then(villageArmyService.addSupport(new VillageArmyCommand.AddVillageSupportCommand(
                        attack.getDefenderVillageId(),
                        attack.getAttackerVillageId(),
                        attack.getBattleResult().getFinalAttackerArmy()
                )))
                .then();
    }

    private Mono<Void> destroyDefenderVillageWall(Attack attack) {
        if (attack.getBattleResult().getNumberOfWallLevelDestroyed() == 0) {
            return Mono.empty();
        }
        return villageInfrastructureService.levelDown(new VillageInfrastructureCommand.LevelDownVillageInfrastructureBuildingCommand(
                attack.getDefenderVillageId(), BuildingType.WALL, attack.getBattleResult().getNumberOfWallLevelDestroyed()
        )).then();
    }

    private Mono<Void> subtractDefenderUnits(Attack attack, VillageDto defenderVillage) {
        var totalLosses = attack.getBattleResult().getDefenderArmyLosses();
        var totalDefenderArmy = attack.getBattleResult().getOriginalDefenderArmy();
        Map<UnitType, Double> ratios = totalLosses.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> (double) e.getValue() / totalDefenderArmy.get(e.getKey())));
        Map<ObjectId, Army> lossesByVillageId =
                Stream.concat(defenderVillage.getVillageArmy().getVillageSupports().stream(),
                                Stream.of(new VillageArmyDto.VillageSupportDto(defenderVillage.getId(), defenderVillage.getVillageArmy().getVillageArmy())))
                        .collect(Collectors.toMap(vs -> vs.getVillageId(), vs -> {
                            var lossesArmy = Army.of(vs.getArmy());
                            lossesArmy.multiply(ratios);
                            return lossesArmy;
                        }));
        return Flux.fromIterable(lossesByVillageId.entrySet())
                .flatMap(e -> villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(e.getKey(), e.getValue()))
                        .flatMap(unused -> villageArmyService.subtract(new VillageArmyCommand.SubtractUnitsCommand(e.getKey(), e.getValue())))
                ).collectList()
                .then();
    }

    private Mono<Void> subtractAttackerUnits(Attack attack) {
        return villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(attack.getAttackerVillageId(), attack.getBattleResult().getAttackerArmyLosses()))
                .then(villageArmyService.subtract(new VillageArmyCommand.SubtractUnitsCommand(attack.getAttackerVillageId(), attack.getBattleResult().getAttackerArmyLosses())))
                .then();
    }

    @Override
    public Mono<ArmyWalk> sendArmy(ArmyWalkCommand.SendArmyCommand command) {
        return Mono.fromCallable(() ->
                Mono.zip(
                                unitDomainRepository.fetchAll(),
                                villageDtoQueryService.fetchById(command.getFromVillageId()),
                                villageDtoQueryService.fetchById(command.getToVillageId())
                        )
                        .flatMap(t -> {
                            var units = t.getT1();
                            var attackerVillage = t.getT2();
                            var defenderVillage = t.getT3();
                            var domain = new ArmyWalk(command.getType(), units,
                                    new ArmyWalk.ArmyWalkVillage(attackerVillage.getOwnerId(), command.getFromVillageId(), attackerVillage.getVillagePosition().getPosition()),
                                    new ArmyWalk.ArmyWalkVillage(defenderVillage.getOwnerId(), command.getToVillageId(), defenderVillage.getVillagePosition().getPosition()),
                                    command.getArmy(), command.getResource(),
                                    command.getOfficers()
                            );
                            return removeOfficers(attackerVillage.getOwnerId(), command.getOfficers())
                                    .then(villageArmyService.block(new VillageArmyCommand.BlockUnitsCommand(command.getFromVillageId(), command.getArmy())))
                                    .then(mongoTemplate.save(domain));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("sending army to the village: {}", command.getToVillageId())
        ).flatMap(Function.identity()).doOnSuccess(d ->
                logger.debug("sent army to the village: {}", d.getArmyWalkId())
        ).doOnError(e ->
                logger.error("exception occurred while sending army to the village: {}, exception: {}", command.getToVillageId(), e.toString())
        );
    }

    private Mono<Void> removeOfficers(ObjectId playerId, Officers officers) {
        return Mono.when(
                officers.getGrandmaster() ? inventoryService.remove(new InventoryCommand.RemoveItemCommand(playerId, new OfficerItem(OfficerType.GRANDMASTER), 1)) : Mono.empty(),
                officers.getMasterOfLoot() ? inventoryService.remove(new InventoryCommand.RemoveItemCommand(playerId, new OfficerItem(OfficerType.MASTER_OF_LOOT), 1)) : Mono.empty(),
                officers.getMedic() ? inventoryService.remove(new InventoryCommand.RemoveItemCommand(playerId, new OfficerItem(OfficerType.MEDIC), 1)) : Mono.empty(),
                officers.getDeceiver() ? inventoryService.remove(new InventoryCommand.RemoveItemCommand(playerId, new OfficerItem(OfficerType.DECEIVER), 1)) : Mono.empty(),
                officers.getRanger() ? inventoryService.remove(new InventoryCommand.RemoveItemCommand(playerId, new OfficerItem(OfficerType.RANGER), 1)) : Mono.empty(),
                officers.getTactician() ? inventoryService.remove(new InventoryCommand.RemoveItemCommand(playerId, new OfficerItem(OfficerType.TACTICIAN), 1)) : Mono.empty()
        );
    }


}
