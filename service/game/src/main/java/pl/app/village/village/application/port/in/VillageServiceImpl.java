package pl.app.village.village.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.army.recruiter.application.port.in.RecruiterCommand;
import pl.app.army.recruiter.application.port.in.RecruiterService;
import pl.app.army.village_army.application.port.in.VillageArmyCommand;
import pl.app.army.village_army.application.port.in.VillageArmyService;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderService;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.map.village_position.application.port.in.VillagePositionCommand;
import pl.app.map.village_position.application.port.in.VillagePositionService;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.village.loyalty.application.port.in.VillageLoyaltyCommand;
import pl.app.village.loyalty.application.port.in.VillageLoyaltyService;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.domain.VillageEvent;
import pl.app.village.village.application.domain.VillageType;
import pl.app.village.village_effect.application.port.in.VillageEffectCommand;
import pl.app.village.village_effect.application.port.in.VillageEffectService;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static pl.app.building.building.model.BuildingType.*;


@Service
@RequiredArgsConstructor
class VillageServiceImpl implements VillageService {
    private static final Logger logger = LoggerFactory.getLogger(VillageServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageDomainRepository villageDomainRepository;

    private final VillageResourceService villageResourceService;
    private final VillagePositionService villagePositionService;
    private final VillageArmyService villageArmyService;
    private final VillageInfrastructureService villageInfrastructureService;
    private final VillageLoyaltyService villageLoyaltyService;
    private final BuilderService builderService;
    private final RecruiterService recruiterService;
    private final VillageEffectService villageEffectService;

    @Override
    public Mono<Village> cratePlayerVillage(VillageCommand.CreatePlayerVillageCommand command) {
        return Mono.fromCallable(() -> {
            var domain = new Village(VillageType.PLAYER, command.getPlayerId());
            var event = new VillageEvent.VillageCreatedEvent(domain.getId(), domain.getType(), domain.getOwnerId());
            return villagePositionService.crate(new VillagePositionCommand.CreateVillagePositionCommand(domain.getId()))
                    .then(villageResourceService.crate(new VillageResourceCommand.CreateVillageResourceCommand(domain.getId())))
                    .then(villageInfrastructureService.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(domain.getId())))
                    .then(villageArmyService.crate(new VillageArmyCommand.CreateVillageArmyCommand(domain.getId())))
                    .then(builderService.crate(new BuilderCommand.CreateBuilderCommand(domain.getId())))
                    .then(recruiterService.crate(new RecruiterCommand.CreateRecruiterCommand(domain.getId())))
                    .then(villageLoyaltyService.create(new VillageLoyaltyCommand.CreateVillageLoyaltyCommand(domain.getId())))
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

    @Override
    public Mono<Village> crateBarbarianVillage(VillageCommand.CreateBarbarianVillageCommand command) {
        return Mono.fromCallable(() -> {
            var domain = new Village(VillageType.BARBARIAN, null);
            var event = new VillageEvent.VillageCreatedEvent(domain.getId(), domain.getType(), domain.getOwnerId());
            return villagePositionService.crate(new VillagePositionCommand.CreateVillagePositionCommand(domain.getId()))
                    .then(villageResourceService.crate(new VillageResourceCommand.CreateVillageResourceCommand(domain.getId())))
                    .then(villageInfrastructureService.crate(new VillageInfrastructureCommand.CreateVillageInfrastructureCommand(domain.getId())))
                    .then(villageArmyService.crate(new VillageArmyCommand.CreateVillageArmyCommand(domain.getId())))
                    .then(builderService.crate(new BuilderCommand.CreateBuilderCommand(domain.getId())))
                    .then(recruiterService.crate(new RecruiterCommand.CreateRecruiterCommand(domain.getId())))
                    .then(villageLoyaltyService.create(new VillageLoyaltyCommand.CreateVillageLoyaltyCommand(domain.getId())))
                    .then(villageEffectService.crate(new VillageEffectCommand.CreateVillageEffectCommand(domain.getId())))
                    .then(mongoTemplate.insert(domain))
                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageCreated().getName(), domain.getId(), event)))
                    .then(villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(domain.getId(), CLAY_PIT, 8)))
                    .then(villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(domain.getId(), IRON_MINE, 8)))
                    .then(villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(domain.getId(), TIMBER_CAMP, 8)))
                    .thenReturn(domain);
        }).doOnSubscribe(subscription ->
                logger.debug("crating barbarian village")
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created barbarian village: {}", domain.getId())
        ).doOnError(e ->
                logger.error("exception occurred while crating barbarian village, exception: {}", e.toString())
        );
    }

    @Override
    public Mono<Village> conquerVillage(VillageCommand.ConquerVillageCommand command) {
        return Mono.fromCallable(() -> villageDomainRepository.fetchById(command.getVillageId())
                .flatMap(domain -> {
                    ObjectId currentOwnerId = domain.getOwnerId();
                    domain.setOwnerId(command.getNewOwnerId());
                    var event = new VillageEvent.VillageConqueredEvent(domain.getId(), domain.getOwnerId(), currentOwnerId);
                    return mongoTemplate.save(domain)
                            .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageConquered().getName(), domain.getId(), event)))
                            .thenReturn(domain);
                })
        ).doOnSubscribe(subscription ->
                logger.debug("conquering village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("conquered village: {}", command.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while conquering village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }
}
