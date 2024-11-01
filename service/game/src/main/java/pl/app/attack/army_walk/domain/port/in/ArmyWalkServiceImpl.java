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
import pl.app.unit.unit.application.domain.UnitType;
import pl.app.unit.unit.application.port.in.UnitDomainRepository;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import pl.app.unit.village_army.query.dto.VillageArmyDto;
import pl.app.village.village.query.VillageDtoQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
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
        logger.debug("finishing army return: {}", domain.getArmyWalkId());
        return Mono.defer(() -> {
            domain.markAsProcessed();
            // unblock army, add resources
            return villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(domain.getTo().getVillageId(), domain.getArmy()))
                    .flatMap(unused -> villageResourceService.add(new VillageResourceCommand.AddResourceCommand(domain.getTo().getVillageId(), domain.getResource())))
                    .flatMap(unused -> mongoTemplate.save(domain))
                    .doOnSuccess(d -> logger.debug("finished army return: {}", d.getArmyWalkId()));
        });
    }

    Mono<ArmyWalk> processAttack(ArmyWalk domain) {
        logger.debug("finishing attack: {}", domain.getArmyWalkId());
        return Mono.zip(
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
                            new Attack.AttackedVillage(defenderVillage.getVillageResource().getResource(), defenderVillage.getVillageInfrastructure().getBuildings().getWall().getLevel()), attackerFaithBonus, defenderFaithBonus
                    );
                    if (domain.getOfficers().getRanger() && !attack.getBattleResult().isAttackerWin()) {
                        var returnArmyWalk = new ArmyWalk(ArmyWalkType.RETURN, units,
                                domain.getTo(),
                                domain.getFrom(),
                                domain.getArmy(), Resource.zero(),
                                new Officers()
                        );
                        return mongoTemplate.save(returnArmyWalk)
                                .then(mongoTemplate.save(domain));
                    }
                    return mongoTemplate.save(domain)
                            .flatMap(d -> mongoTemplate.insert(attack))
                            // subtract attacker units
                            .flatMap(d ->
                                    villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(d.getAttackerVillageId(), d.getBattleResult().getAttackerArmyLosses()))
                                            .flatMap(unused -> villageArmyService.subtract(new VillageArmyCommand.SubtractUnitsCommand(d.getAttackerVillageId(), d.getBattleResult().getAttackerArmyLosses())))
                                            .thenReturn(d)
                            )
                            // subtract defender units
                            .flatMap(d -> {
                                        var totalLosses = d.getBattleResult().getDefenderArmyLosses();
                                        var totalDefenderArmy = d.getBattleResult().getOriginalDefenderArmy();
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
                                                .thenReturn(d);
                                    }
                            )
                            // destroy wall
                            .flatMap(d -> {
                                if (d.getBattleResult().getNumberOfWallLevelDestroyed() > 0) {
                                    return villageInfrastructureService.levelDown(new VillageInfrastructureCommand.LevelDownVillageInfrastructureBuildingCommand(
                                            d.getDefenderVillageId(), BuildingType.WALL, d.getBattleResult().getNumberOfWallLevelDestroyed()
                                    )).thenReturn(d);
                                } else return Mono.just(d);
                            })
                            // attacker win:
                            // subtract resources, start walk
                            .flatMap(d -> {
                                if (!d.getBattleResult().isAttackerWin() || d.getReturnArmyWalk().isEmpty()) {
                                    return Mono.just(d);
                                }
                                return villageResourceService.subtract(new VillageResourceCommand.SubtractResourceCommand(d.getDefenderVillageId(), d.getPlunderedResource()))
                                        .flatMap(unused -> mongoTemplate.save(d.getReturnArmyWalk().get()))
                                        .thenReturn(d);
                            })
                            .thenReturn(domain)
                            .doOnSuccess(d -> logger.debug("finished processing attack: {}", d.getArmyWalkId()));

                });
    }

    @Override
    public Mono<ArmyWalk> sendArmy(ArmyWalkCommand.SendArmyCommand command) {
        logger.debug("sending army to the village: {}", command.getToVillageId());
        return Mono.zip(
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
                            .then(mongoTemplate.save(domain))
                            .doOnSuccess(unused -> logger.debug("sent army: {}", domain.getArmyWalkId()));
                })
                .doOnError(e -> logger.error("exception occurred while sending army to the village: {}, exception: {}", command.getToVillageId(), e.getMessage()))
                ;
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
