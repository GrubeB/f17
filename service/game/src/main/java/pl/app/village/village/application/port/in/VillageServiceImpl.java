package pl.app.village.village.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderService;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.map.village_position.application.port.in.VillagePositionCommand;
import pl.app.map.village_position.application.port.in.VillagePositionService;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.unit.recruiter.application.port.in.RecruiterCommand;
import pl.app.unit.recruiter.application.port.in.RecruiterService;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.domain.VillageEvent;
import pl.app.village.village.application.domain.VillageType;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class VillageServiceImpl implements VillageService {
    private static final Logger logger = LoggerFactory.getLogger(VillageServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageResourceService villageResourceService;
    private final VillagePositionService villagePositionService;
    private final VillageArmyService villageArmyService;
    private final VillageInfrastructureService villageInfrastructureService;
    private final BuilderService builderService;
    private final RecruiterService recruiterService;

    @Override
    public Mono<Village> crate(VillageCommand.CreatePlayerVillageCommand command) {
        logger.debug("crating village: {}", command.getPlayerId());
        return Mono.defer(() -> {
                    var domain = new Village(VillageType.PLAYER, command.getPlayerId());
                    return villagePositionService.crate(new VillagePositionCommand.CreateVillagePositionCommand(domain.getId()))
                            .flatMap(unused -> villageResourceService.crate(new VillageResourceCommand.CreateVillageResourceCommand(domain.getId())))
                            .flatMap(unused -> villageInfrastructureService.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(domain.getId())))
                            .flatMap(unused -> villageArmyService.crate(new VillageArmyCommand.CreateVillageArmyCommand(domain.getId())))
                            .flatMap(unused -> builderService.crate(new BuilderCommand.CreateBuilderCommand(domain.getId())))
                            .flatMap(unused -> recruiterService.crate(new RecruiterCommand.CreateRecruiterCommand(domain.getId())))
                            .flatMap(unused -> {
                                var event = new VillageEvent.VillageCreatedEvent(domain.getId(), domain.getType(), domain.getOwnerId());
                                return mongoTemplate.insert(domain)
                                        .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageCreated().getName(), saved.getId(), event)).thenReturn(saved))
                                        .doOnSuccess(saved -> logger.debug("created village: {}", saved.getId()));
                            });
                })
                .doOnError(e -> logger.error("exception occurred while crating village: {}, exception: {}", command.getPlayerId(), e.getMessage()));
    }
}
