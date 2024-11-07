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
import pl.app.village.village_effect.application.port.in.VillageEffectCommand;
import pl.app.village.village_effect.application.port.in.VillageEffectService;
import reactor.core.publisher.Mono;

import java.util.function.Function;


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
    private final VillageEffectService villageEffectService;

    @Override
    public Mono<Village> crate(VillageCommand.CreatePlayerVillageCommand command) {
        return Mono.fromCallable(() -> {
            var domain = new Village(VillageType.PLAYER, command.getPlayerId());
            var event = new VillageEvent.VillageCreatedEvent(domain.getId(), domain.getType(), domain.getOwnerId());
            return villagePositionService.crate(new VillagePositionCommand.CreateVillagePositionCommand(domain.getId()))
                    .then(villageResourceService.crate(new VillageResourceCommand.CreateVillageResourceCommand(domain.getId())))
                    .then(villageInfrastructureService.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(domain.getId())))
                    .then(villageArmyService.crate(new VillageArmyCommand.CreateVillageArmyCommand(domain.getId())))
                    .then(builderService.crate(new BuilderCommand.CreateBuilderCommand(domain.getId())))
                    .then(recruiterService.crate(new RecruiterCommand.CreateRecruiterCommand(domain.getId())))
                    .then(villageEffectService.crate(new VillageEffectCommand.CreateVillageEffectCommand(domain.getId())))
                    .then(mongoTemplate.insert(domain))
                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageCreated().getName(), domain.getId(), event)))
                    .thenReturn(domain);
        }).doOnSubscribe(subscription ->
                logger.debug("crating village for a player: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("crated village: {}, for a player: {}", domain.getId(), command.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while crating village for a player: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }
}
