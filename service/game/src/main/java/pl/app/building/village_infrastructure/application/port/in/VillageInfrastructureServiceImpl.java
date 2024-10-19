package pl.app.building.village_infrastructure.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.port.in.BuildingLevelDomainRepository;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructure;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructureEvent;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;

import static pl.app.building.building.application.domain.BuildingType.*;


@Service
@RequiredArgsConstructor
class VillageInfrastructureServiceImpl implements VillageInfrastructureService {
    private static final Logger logger = LoggerFactory.getLogger(VillageInfrastructureServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageInfrastructureDomainRepository villageInfrastructureDomainRepository;
    private final BuildingLevelDomainRepository buildingLevelDomainRepository;

    @Override
    public Mono<VillageInfrastructure> crate(VillageInfrastructureCommand.CreateVillageInfrastructureCommand command) {
        logger.debug("crating village infrastructure: {}", command.getVillageId());
        return Mono.defer(() -> {
                    var domain = new VillageInfrastructure(command.getVillageId());
                    var event = new VillageInfrastructureEvent.VillageInfrastructureCreatedEvent(domain.getVillageId());
                    return Mono.just(domain)
                            .flatMap(d -> innerLevelUp(d, HEADQUARTERS, 1))
                            .flatMap(d -> innerLevelUp(d, WAREHOUSE, 1))
                            .flatMap(d -> innerLevelUp(d, FARM, 1))
                            .flatMap(d -> mongoTemplate.insert(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageInfrastructureCreated().getName(), saved.getVillageId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> logger.debug("created village infrastructure: {}", saved.getVillageId()));
                })
                .doOnError(e -> logger.error("exception occurred while crating village infrastructure: {}, exception: {}", command.getVillageId(), e.getMessage()));
    }

    @Override
    public Mono<VillageInfrastructure> levelUp(VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand command) {
        logger.debug("leveling up village infrastructure building: {}", command.getVillageId());
        return villageInfrastructureDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while leveling up village infrastructure building: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> this.innerLevelUp(domain, command.getBuildingType(), command.getNumberOfLevels()))
                .flatMap(mongoTemplate::save)
                .doOnSuccess(saved -> logger.debug("level up village infrastructure building: {}", saved.getVillageId()));
    }

    private Mono<VillageInfrastructure> innerLevelUp(VillageInfrastructure domain, BuildingType buildingType, Integer numberOfLevels) {
        return buildingLevelDomainRepository.fetchAll(buildingType)
                .flatMap(levels -> {
                    VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent event = switch (buildingType) {
                        case ACADEMY -> {
                            var building = domain.getAcademy();
                            building.levelUp(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), ACADEMY, numberOfLevels);
                        }
                        case BARRACKS -> {
                            var building = domain.getBarracks();
                            building.levelUp(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), BARRACKS, numberOfLevels);
                        }
                        case CHAPEL -> {
                            var building = domain.getChapel();
                            building.levelUp(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), CHAPEL, numberOfLevels);
                        }
                        case CHURCH -> {
                            var building = domain.getChurch();
                            building.levelUp(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), CHURCH, numberOfLevels);
                        }
                        case CLAY_PIT -> {
                            var building = domain.getClayPit();
                            building.levelUp(levels, numberOfLevels);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelUpEvent(domain.getVillageId(), CLAY_PIT, numberOfLevels, currentProduction);
                        }
                        case FARM -> {
                            var building = domain.getFarm();
                            var enterProvisions = building.getProvisions();
                            building.levelUp(levels, numberOfLevels);
                            var currentProvisions = building.getProvisions();
                            yield new VillageInfrastructureEvent.VillageInfrastructureFarmBuildingLevelUpEvent(domain.getVillageId(), FARM, numberOfLevels, currentProvisions - enterProvisions);
                        }
                        case HEADQUARTERS -> {
                            var building = domain.getHeadquarters();
                            building.levelUp(levels, numberOfLevels);
                            var currentFinishBuildingDuration = building.getFinishBuildingDuration();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHeadquartersBuildingLevelUpEvent(domain.getVillageId(), HEADQUARTERS, numberOfLevels, currentFinishBuildingDuration);
                        }
                        case HOSPITAL -> {
                            var building = domain.getHospital();
                            building.levelUp(levels, numberOfLevels);
                            var currentBeds = building.getBeds();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHospitalBuildingLevelUpEvent(domain.getVillageId(), HOSPITAL, numberOfLevels, currentBeds);
                        }
                        case IRON_MINE -> {
                            var building = domain.getIronMine();
                            building.levelUp(levels, numberOfLevels);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelUpEvent(domain.getVillageId(), IRON_MINE, numberOfLevels, currentProduction);
                        }
                        case MARKET -> {
                            var building = domain.getMarket();
                            building.levelUp(levels, numberOfLevels);
                            var currentTraderNumber = building.getTraderNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureMarketBuildingLevelUpEvent(domain.getVillageId(), MARKET, numberOfLevels, currentTraderNumber);
                        }
                        case RALLY_POINT -> {
                            var building = domain.getRallyPoint();
                            building.levelUp(levels, numberOfLevels);
                            var currentSpeedIncreaseAgainstBarbarians = building.getSpeedIncreaseAgainstBarbarians();
                            yield new VillageInfrastructureEvent.VillageInfrastructureRallyPointBuildingLevelUpEvent(domain.getVillageId(), RALLY_POINT, numberOfLevels, currentSpeedIncreaseAgainstBarbarians);
                        }
                        case STATUE -> {
                            var building = domain.getStatue();
                            building.levelUp(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelUpEvent(domain.getVillageId(), STATUE, numberOfLevels);
                        }
                        case TAVERN -> {
                            var building = domain.getTavern();
                            building.levelUp(levels, numberOfLevels);
                            var currentSpyNumber = building.getSpyNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureTavernBuildingLevelUpEvent(domain.getVillageId(), TAVERN, numberOfLevels, currentSpyNumber);
                        }
                        case TIMBER_CAMP -> {
                            var building = domain.getTimberCamp();
                            building.levelUp(levels, numberOfLevels);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelUpEvent(domain.getVillageId(), TIMBER_CAMP, numberOfLevels, currentProduction);
                        }
                        case WALL -> {
                            var building = domain.getWall();
                            building.levelUp(levels, numberOfLevels);
                            var currentDefenceIncrease = building.getDefenceIncrease();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWallBuildingLevelUpEvent(domain.getVillageId(), WALL, numberOfLevels, currentDefenceIncrease);
                        }
                        case WAREHOUSE -> {
                            var building = domain.getWarehouse();
                            building.levelUp(levels, numberOfLevels);
                            var currentCapacity = building.getCapacity();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWarehouseBuildingLevelUpEvent(domain.getVillageId(), WAREHOUSE, numberOfLevels, currentCapacity);
                        }
                    };
                    return Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageInfrastructureBuildingLevelUp().getName(), domain.getVillageId(), event)).thenReturn(domain);
                });


    }

    @Override
    public Mono<VillageInfrastructure> levelDown(VillageInfrastructureCommand.LevelDownVillageInfrastructureBuildingCommand command) {
        logger.debug("leveling down village infrastructure building: {}", command.getVillageId());
        return villageInfrastructureDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while leveling down village infrastructure building: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> this.innerLevelDown(domain, command.getBuildingType(), command.getNumberOfLevels()))
                .flatMap(mongoTemplate::save)
                .doOnSuccess(saved -> logger.debug("level down village infrastructure building: {}", saved.getVillageId()));
    }

    private Mono<VillageInfrastructure> innerLevelDown(VillageInfrastructure domain, BuildingType buildingType, Integer numberOfLevels) {
        return buildingLevelDomainRepository.fetchAll(buildingType)
                .flatMap(levels -> {
                    VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent event = switch (buildingType) {
                        case ACADEMY -> {
                            var building = domain.getAcademy();
                            building.levelDown(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), ACADEMY, numberOfLevels);
                        }
                        case BARRACKS -> {
                            var building = domain.getBarracks();
                            building.levelDown(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), BARRACKS, numberOfLevels);
                        }
                        case CHAPEL -> {
                            var building = domain.getChapel();
                            building.levelDown(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), CHAPEL, numberOfLevels);
                        }
                        case CHURCH -> {
                            var building = domain.getChurch();
                            building.levelDown(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), CHURCH, numberOfLevels);
                        }
                        case CLAY_PIT -> {
                            var building = domain.getClayPit();
                            building.levelDown(levels, numberOfLevels);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelDownEvent(domain.getVillageId(), CLAY_PIT, numberOfLevels, currentProduction);
                        }
                        case FARM -> {
                            var building = domain.getFarm();
                            var enterProvisions = building.getProvisions();
                            building.levelDown(levels, numberOfLevels);
                            var currentProvisions = building.getProvisions();
                            yield new VillageInfrastructureEvent.VillageInfrastructureFarmBuildingLevelDownEvent(domain.getVillageId(), FARM, numberOfLevels, currentProvisions - enterProvisions);
                        }
                        case HEADQUARTERS -> {
                            var building = domain.getHeadquarters();
                            building.levelDown(levels, numberOfLevels);
                            var currentFinishBuildingDuration = building.getFinishBuildingDuration();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHeadquartersBuildingLevelDownEvent(domain.getVillageId(), HEADQUARTERS, numberOfLevels, currentFinishBuildingDuration);
                        }
                        case HOSPITAL -> {
                            var building = domain.getHospital();
                            building.levelDown(levels, numberOfLevels);
                            var currentBeds = building.getBeds();
                            yield new VillageInfrastructureEvent.VillageInfrastructureHospitalBuildingLevelDownEvent(domain.getVillageId(), HOSPITAL, numberOfLevels, currentBeds);
                        }
                        case IRON_MINE -> {
                            var building = domain.getIronMine();
                            building.levelDown(levels, numberOfLevels);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelDownEvent(domain.getVillageId(), IRON_MINE, numberOfLevels, currentProduction);
                        }
                        case MARKET -> {
                            var building = domain.getMarket();
                            building.levelDown(levels, numberOfLevels);
                            var currentTraderNumber = building.getTraderNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureMarketBuildingLevelDownEvent(domain.getVillageId(), MARKET, numberOfLevels, currentTraderNumber);
                        }
                        case RALLY_POINT -> {
                            var building = domain.getRallyPoint();
                            building.levelDown(levels, numberOfLevels);
                            var currentSpeedIncreaseAgainstBarbarians = building.getSpeedIncreaseAgainstBarbarians();
                            yield new VillageInfrastructureEvent.VillageInfrastructureRallyPointBuildingLevelDownEvent(domain.getVillageId(), RALLY_POINT, numberOfLevels, currentSpeedIncreaseAgainstBarbarians);
                        }
                        case STATUE -> {
                            var building = domain.getStatue();
                            building.levelDown(levels, numberOfLevels);
                            yield new VillageInfrastructureEvent.VillageInfrastructureBuildingLevelDownEvent(domain.getVillageId(), STATUE, numberOfLevels);
                        }
                        case TAVERN -> {
                            var building = domain.getTavern();
                            building.levelDown(levels, numberOfLevels);
                            var currentSpyNumber = building.getSpyNumber();
                            yield new VillageInfrastructureEvent.VillageInfrastructureTavernBuildingLevelDownEvent(domain.getVillageId(), TAVERN, numberOfLevels, currentSpyNumber);
                        }
                        case TIMBER_CAMP -> {
                            var building = domain.getTimberCamp();
                            building.levelDown(levels, numberOfLevels);
                            int currentProduction = building.getProduction();
                            yield new VillageInfrastructureEvent.VillageInfrastructureResourceBuildingLevelDownEvent(domain.getVillageId(), TIMBER_CAMP, numberOfLevels, currentProduction);
                        }
                        case WALL -> {
                            var building = domain.getWall();
                            building.levelDown(levels, numberOfLevels);
                            var currentDefenceIncrease = building.getDefenceIncrease();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWallBuildingLevelDownEvent(domain.getVillageId(), WALL, numberOfLevels, currentDefenceIncrease);
                        }
                        case WAREHOUSE -> {
                            var building = domain.getWarehouse();
                            building.levelDown(levels, numberOfLevels);
                            var currentCapacity = building.getCapacity();
                            yield new VillageInfrastructureEvent.VillageInfrastructureWarehouseBuildingLevelDownEvent(domain.getVillageId(), WAREHOUSE, numberOfLevels, currentCapacity);
                        }
                    };
                    return Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageInfrastructureBuildingLevelDown().getName(), domain.getVillageId(), event)).thenReturn(domain);
                });


    }
}
