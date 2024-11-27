package pl.app.building.village_infrastructure.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.service.BuildingLevelService;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructure;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructureEvent;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static pl.app.building.building.model.BuildingType.*;


@Service
@RequiredArgsConstructor
class VillageInfrastructureServiceImpl implements VillageInfrastructureService {
    private static final Logger logger = LoggerFactory.getLogger(VillageInfrastructureServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageInfrastructureDomainRepository villageInfrastructureDomainRepository;
    private final BuildingLevelService buildingLevelDomainRepository;

    @Override
    public Mono<VillageInfrastructure> crate(VillageInfrastructureCommand.CreateVillageInfrastructureCommand command) {
        return Mono.fromCallable(() -> {
            var domain = new VillageInfrastructure(command.getVillageId());
            var event = new VillageInfrastructureEvent.VillageInfrastructureCreatedEvent(domain.getVillageId());
            return innerLevelUp(domain, HEADQUARTERS, 1)
                    .then(innerLevelUp(domain, WAREHOUSE, 1))
                    .then(mongoTemplate.insert(domain))
                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageInfrastructureCreated().getName(), domain.getVillageId(), event)))
                    .thenReturn(domain);
        }).doOnSubscribe(subscription ->
                logger.debug("crating village infrastructure for village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created village infrastructure for village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while crating village infrastructure for village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageInfrastructure> levelUp(VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand command) {
        return Mono.fromCallable(() ->
                villageInfrastructureDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> this.innerLevelUp(domain, command.getBuildingType(), command.getToLevel())
                                .then(mongoTemplate.save(domain))
                        )
        ).doOnSubscribe(subscription ->
                logger.debug("leveling up village infrastructure building: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("level up village infrastructure building: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while leveling up village infrastructure building: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    private Mono<VillageInfrastructure> innerLevelUp(VillageInfrastructure domain, BuildingType buildingType, Integer toLevel) {
        return buildingLevelDomainRepository.fetchAll(buildingType)
                .flatMap(levels -> {
                    VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent event = switch (buildingType) {
                        case ACADEMY -> {
                            var building = domain.getBuildings().getAcademy();
                            building.levelUp(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), ACADEMY, toLevel);
                        }
                        case BARRACKS -> {
                            var building = domain.getBuildings().getBarracks();
                            building.levelUp(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), BARRACKS, toLevel);
                        }
                        case CHAPEL -> {
                            var building = domain.getBuildings().getChapel();
                            building.levelUp(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), CHAPEL, toLevel);
                        }
                        case CHURCH -> {
                            var building = domain.getBuildings().getChurch();
                            building.levelUp(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), CHURCH, toLevel);
                        }
                        case CLAY_PIT -> {
                            var building = domain.getBuildings().getClayPit();
                            building.levelUp(levels, toLevel);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelUpEvent(domain.getVillageId(), CLAY_PIT, toLevel, currentProduction);
                        }
                        case FARM -> {
                            var building = domain.getBuildings().getFarm();
                            var enterProvisions = building.getProvisions();
                            building.levelUp(levels, toLevel);
                            var currentProvisions = building.getProvisions();
                            yield new VillageInfrastructureEvent.VillageInfrastructureFarmBuildingLevelUpEvent(domain.getVillageId(), FARM, toLevel, currentProvisions - enterProvisions);
                        }
                        case HEADQUARTERS -> {
                            var building = domain.getBuildings().getHeadquarters();
                            building.levelUp(levels, toLevel);
                            var currentFinishBuildingDuration = building.getFinishBuildingDuration();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHeadquartersBuildingLevelUpEvent(domain.getVillageId(), HEADQUARTERS, toLevel, currentFinishBuildingDuration);
                        }
                        case HOSPITAL -> {
                            var building = domain.getBuildings().getHospital();
                            building.levelUp(levels, toLevel);
                            var currentBeds = building.getBeds();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHospitalBuildingLevelUpEvent(domain.getVillageId(), HOSPITAL, toLevel, currentBeds);
                        }
                        case IRON_MINE -> {
                            var building = domain.getBuildings().getIronMine();
                            building.levelUp(levels, toLevel);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelUpEvent(domain.getVillageId(), IRON_MINE, toLevel, currentProduction);
                        }
                        case MARKET -> {
                            var building = domain.getBuildings().getMarket();
                            building.levelUp(levels, toLevel);
                            var currentTraderNumber = building.getTraderNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureMarketBuildingLevelUpEvent(domain.getVillageId(), MARKET, toLevel, currentTraderNumber);
                        }
                        case RALLY_POINT -> {
                            var building = domain.getBuildings().getRallyPoint();
                            building.levelUp(levels, toLevel);
                            var currentSpeedIncreaseAgainstBarbarians = building.getSpeedIncreaseAgainstBarbarians();
                            yield new VillageInfrastructureEvent.VillageInfrastructureRallyPointBuildingLevelUpEvent(domain.getVillageId(), RALLY_POINT, toLevel, currentSpeedIncreaseAgainstBarbarians);
                        }
                        case STATUE -> {
                            var building = domain.getBuildings().getStatue();
                            building.levelUp(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), STATUE, toLevel);
                        }
                        case TAVERN -> {
                            var building = domain.getBuildings().getTavern();
                            building.levelUp(levels, toLevel);
                            var currentSpyNumber = building.getSpyNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureTavernBuildingLevelUpEvent(domain.getVillageId(), TAVERN, toLevel, currentSpyNumber);
                        }
                        case TIMBER_CAMP -> {
                            var building = domain.getBuildings().getTimberCamp();
                            building.levelUp(levels, toLevel);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelUpEvent(domain.getVillageId(), TIMBER_CAMP, toLevel, currentProduction);
                        }
                        case WALL -> {
                            var building = domain.getBuildings().getWall();
                            building.levelUp(levels, toLevel);
                            var currentDefenceIncrease = building.getDefenceIncrease();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWallBuildingLevelUpEvent(domain.getVillageId(), WALL, toLevel, currentDefenceIncrease);
                        }
                        case WAREHOUSE -> {
                            var building = domain.getBuildings().getWarehouse();
                            building.levelUp(levels, toLevel);
                            var currentCapacity = building.getCapacity();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWarehouseBuildingLevelUpEvent(domain.getVillageId(), WAREHOUSE, toLevel, currentCapacity);
                        }
                    };
                    return Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageInfrastructureBuildingLevelUp().getName(), domain.getVillageId(), event)).thenReturn(domain);
                });


    }

    @Override
    public Mono<VillageInfrastructure> levelDown(VillageInfrastructureCommand.LevelDownVillageInfrastructureBuildingCommand command) {
        return Mono.fromCallable(() ->
                villageInfrastructureDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> this.innerLevelDown(domain, command.getBuildingType(), command.getToLevel())
                                .then(mongoTemplate.save(domain))
                        )
        ).doOnSubscribe(subscription ->
                logger.debug("leveling down village infrastructure building: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("level down village infrastructure building: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while leveling down village infrastructure building: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    private Mono<VillageInfrastructure> innerLevelDown(VillageInfrastructure domain, BuildingType buildingType, Integer toLevel) {
        return buildingLevelDomainRepository.fetchAll(buildingType)
                .flatMap(levels -> {
                    VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent event = switch (buildingType) {
                        case ACADEMY -> {
                            var building = domain.getBuildings().getAcademy();
                            building.levelDown(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), ACADEMY, toLevel);
                        }
                        case BARRACKS -> {
                            var building = domain.getBuildings().getBarracks();
                            building.levelDown(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), BARRACKS, toLevel);
                        }
                        case CHAPEL -> {
                            var building = domain.getBuildings().getChapel();
                            building.levelDown(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), CHAPEL, toLevel);
                        }
                        case CHURCH -> {
                            var building = domain.getBuildings().getChurch();
                            building.levelDown(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), CHURCH, toLevel);
                        }
                        case CLAY_PIT -> {
                            var building = domain.getBuildings().getClayPit();
                            building.levelDown(levels, toLevel);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelDownEvent(domain.getVillageId(), CLAY_PIT, toLevel, currentProduction);
                        }
                        case FARM -> {
                            var building = domain.getBuildings().getFarm();
                            var enterProvisions = building.getProvisions();
                            building.levelDown(levels, toLevel);
                            var currentProvisions = building.getProvisions();
                            yield new VillageInfrastructureEvent.VillageInfrastructureFarmBuildingLevelDownEvent(domain.getVillageId(), FARM, toLevel, currentProvisions - enterProvisions);
                        }
                        case HEADQUARTERS -> {
                            var building = domain.getBuildings().getHeadquarters();
                            building.levelDown(levels, toLevel);
                            var currentFinishBuildingDuration = building.getFinishBuildingDuration();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHeadquartersBuildingLevelDownEvent(domain.getVillageId(), HEADQUARTERS, toLevel, currentFinishBuildingDuration);
                        }
                        case HOSPITAL -> {
                            var building = domain.getBuildings().getHospital();
                            building.levelDown(levels, toLevel);
                            var currentBeds = building.getBeds();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHospitalBuildingLevelDownEvent(domain.getVillageId(), HOSPITAL, toLevel, currentBeds);
                        }
                        case IRON_MINE -> {
                            var building = domain.getBuildings().getIronMine();
                            building.levelDown(levels, toLevel);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelDownEvent(domain.getVillageId(), IRON_MINE, toLevel, currentProduction);
                        }
                        case MARKET -> {
                            var building = domain.getBuildings().getMarket();
                            building.levelDown(levels, toLevel);
                            var currentTraderNumber = building.getTraderNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureMarketBuildingLevelDownEvent(domain.getVillageId(), MARKET, toLevel, currentTraderNumber);
                        }
                        case RALLY_POINT -> {
                            var building = domain.getBuildings().getRallyPoint();
                            building.levelDown(levels, toLevel);
                            var currentSpeedIncreaseAgainstBarbarians = building.getSpeedIncreaseAgainstBarbarians();
                            yield new VillageInfrastructureEvent.VillageInfrastructureRallyPointBuildingLevelDownEvent(domain.getVillageId(), RALLY_POINT, toLevel, currentSpeedIncreaseAgainstBarbarians);
                        }
                        case STATUE -> {
                            var building = domain.getBuildings().getStatue();
                            building.levelDown(levels, toLevel);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), STATUE, toLevel);
                        }
                        case TAVERN -> {
                            var building = domain.getBuildings().getTavern();
                            building.levelDown(levels, toLevel);
                            var currentSpyNumber = building.getSpyNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureTavernBuildingLevelDownEvent(domain.getVillageId(), TAVERN, toLevel, currentSpyNumber);
                        }
                        case TIMBER_CAMP -> {
                            var building = domain.getBuildings().getTimberCamp();
                            building.levelDown(levels, toLevel);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelDownEvent(domain.getVillageId(), TIMBER_CAMP, toLevel, currentProduction);
                        }
                        case WALL -> {
                            var building = domain.getBuildings().getWall();
                            building.levelDown(levels, toLevel);
                            var currentDefenceIncrease = building.getDefenceIncrease();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWallBuildingLevelDownEvent(domain.getVillageId(), WALL, toLevel, currentDefenceIncrease);
                        }
                        case WAREHOUSE -> {
                            var building = domain.getBuildings().getWarehouse();
                            building.levelDown(levels, toLevel);
                            var currentCapacity = building.getCapacity();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWarehouseBuildingLevelDownEvent(domain.getVillageId(), WAREHOUSE, toLevel, currentCapacity);
                        }
                    };
                    return Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageInfrastructureBuildingLevelDown().getName(), domain.getVillageId(), event)).thenReturn(domain);
                });


    }
}
