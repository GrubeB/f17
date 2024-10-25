package pl.app.attack.army_walk.domain.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.application.Attack;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.unit.unit.application.port.in.UnitDomainRepository;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import pl.app.village.village.query.VillageDtoQueryService;
import reactor.core.publisher.Mono;


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


    @Override
    public Mono<ArmyWalk> process(ArmyWalkCommand.ProcessArmyArrivalCommand command) {
        return armyWalkDomainRepository.fetchById(command.getWalkId())
                .flatMap(armyWalk ->
                        switch (armyWalk.getType()) {
                            case ATTACK -> processAttack(armyWalk)
                                    .map(attack -> attack.getReturnArmyWalk().orElse(null));
                            case RETURN -> processReturn(armyWalk);
                            case SUPPORT -> throw new RuntimeException("not implemented yet");
                            case RELOCATE -> throw new RuntimeException("not implemented yet");
                        }
                );

    }

    Mono<ArmyWalk> processReturn(ArmyWalk domain) {
        logger.debug("finishing army return: {}", domain.getArmyWalkId());
        return
                Mono.defer(() -> {
                    domain.markAsProcessed();
                    // unblock army, add resources
                    return villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(domain.getTo().getVillageId(), domain.getArmy()))
                            .flatMap(unused -> villageResourceService.add(new VillageResourceCommand.AddResourceCommand(domain.getTo().getVillageId(), domain.getResource())))
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .doOnSuccess(d -> logger.debug("finished army return: {}", d.getArmyWalkId()));
                });
    }

    Mono<Attack> processAttack(ArmyWalk domain) {
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
                    var attack = new Attack(domain, domain.getArmy(), defenderVillage.getVillageArmy().getVillageArmy(), units,
                            new Attack.AttackVillage(defenderVillage.getVillageResource().getResource(), defenderVillage.getVillageInfrastructure().getBuildings().getWall().getLevel()));
                    return mongoTemplate.save(domain)
                            .flatMap(d -> mongoTemplate.insert(attack))
                            // subtract attacker&defender units
                            .flatMap(d ->
                                    villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(d.getAttackerVillageId(), d.getBattleResult().getAttackerArmyLosses()))
                                            .flatMap(unused -> villageArmyService.subtract(new VillageArmyCommand.SubtractUnitsCommand(d.getAttackerVillageId(), d.getBattleResult().getAttackerArmyLosses())))
                                            .thenReturn(d)
                            )
                            .flatMap(d ->
                                    villageArmyService.unblock(new VillageArmyCommand.UnblockUnitsCommand(d.getDefenderVillageId(), d.getBattleResult().getDefenderArmyLosses()))
                                            .flatMap(unused -> villageArmyService.subtract(new VillageArmyCommand.SubtractUnitsCommand(d.getDefenderVillageId(), d.getBattleResult().getDefenderArmyLosses())))
                                            .thenReturn(d)
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
                            .doOnSuccess(d -> logger.debug("finished attack: {}", d.getAttackId()));

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
                .doOnError(e -> logger.error("exception occurred while sending army to the village: {}, exception: {}", command.getToVillageId(), e.getMessage()))
                .flatMap(t -> {
                    var units = t.getT1();
                    var attackerVillage = t.getT2();
                    var defenderVillage = t.getT3();
                    var domain = new ArmyWalk(command.getType(), units,
                            new ArmyWalk.ArmyWalkVillage(command.getFromPlayerId(), command.getFromVillageId(), attackerVillage.getVillagePosition().getPosition()),
                            new ArmyWalk.ArmyWalkVillage(command.getToPlayerId(), command.getToVillageId(), defenderVillage.getVillagePosition().getPosition()),
                            command.getArmy(), command.getResource()
                    );
                    return villageArmyService.block(new VillageArmyCommand.BlockUnitsCommand(command.getFromVillageId(), command.getArmy()))
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .doOnSuccess(saved -> logger.debug("sent army: {}", saved.getArmyWalkId()));
                });
    }


}
