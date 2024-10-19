package pl.app.building.builder.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.building.builder.application.domain.Builder;
import pl.app.building.builder.application.domain.BuilderEvent;
import pl.app.building.builder.application.domain.BuilderException;
import pl.app.building.building.application.port.in.BuildingLevelDomainRepository;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureDomainRepository;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Service
@RequiredArgsConstructor
class BuilderServiceImpl implements BuilderService {
    private static final Logger logger = LoggerFactory.getLogger(BuilderServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final BuilderDomainRepository builderDomainRepository;
    private final VillageInfrastructureDomainRepository villageInfrastructureDomainRepository;
    private final BuildingLevelDomainRepository buildingLevelDomainRepository;
    private final VillageResourceService villageResourceService;
    private final VillageInfrastructureService villageInfrastructureService;

    @Override
    public Mono<Builder> crate(BuilderCommand.CreateBuilderCommand command) {
        logger.debug("crating builder for village: {}", command.getVillageId());
        return mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), Builder.class)
                .flatMap(exist -> exist ? Mono.error(BuilderException.DuplicatedBuilderException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating builder for village: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new Builder(command.getVillageId(), 7);
                    var event = new BuilderEvent.BuilderCreatedEvent(domain.getVillageId());
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getBuilderCreated().getName(), saved.getVillageId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> logger.debug("created builder for village: {}", saved.getVillageId()));
                }));
    }

    @Override
    public Mono<Builder> add(BuilderCommand.AddBuildingToConstructCommand command) {
        logger.debug("adding building to construct in village: {}", command.getVillageId());
        return Mono.zip(
                        builderDomainRepository.fetchByVillageId(command.getVillageId()),
                        villageInfrastructureDomainRepository.fetchByVillageId(command.getVillageId()),
                        buildingLevelDomainRepository.fetchAll(command.getType())
                )
                .doOnError(e -> logger.error("exception occurred while adding building to construct: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(t -> {
                    var domain = t.getT1();
                    var infrastructure = t.getT2();
                    var buildingLevels = t.getT3();
                    var building = infrastructure.getBuildingByType(command.getType());
                    var construct = domain.addConstruct(command.getType(), building.getLevel(), buildingLevels);

                    var event = new BuilderEvent.ConstructAddedEvent(domain.getVillageId(), construct.getType(), construct.getToLevel(), construct.getFrom(), construct.getTo());
                    return villageResourceService.subtract(new VillageResourceCommand.SubtractResourceCommand(command.getVillageId(), construct.getCost()))
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getConstructAdded().getName(), saved.getVillageId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> logger.debug("added building to construct in village: {}", saved.getVillageId()));
                });
    }

    @Override
    public Mono<Builder> finish(BuilderCommand.FinishConstructCommand command) {
        logger.debug("finishing construct in village: {}", command.getVillageId());
        return builderDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while finishing construct in village: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    var construct = domain.finishConstruct();
                    if (construct.isEmpty()) {
                        return Mono.just(domain);
                    }
                    return villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(command.getVillageId(), construct.get().getType(), 1))
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .doOnSuccess(saved -> logger.debug("finished construct in village: {}", saved.getVillageId()));
                });
    }

    @Override
    public Mono<Builder> remove(BuilderCommand.RemoveBuildingToConstructCommand command) {
        logger.debug("removing building to construct in village: {}", command.getVillageId());
        return builderDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while removing building to construct: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    var construct = domain.removeConstruct(command.getType());
                    if (Objects.isNull(construct)) {
                        return Mono.just(domain);
                    }
                    var event = new BuilderEvent.ConstructRemovedEvent(domain.getVillageId(), construct.getType(), construct.getToLevel(), construct.getFrom(), construct.getTo());
                    return villageResourceService.add(new VillageResourceCommand.AddResourceCommand(command.getVillageId(), construct.getCost()))
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getConstructRemoved().getName(), saved.getVillageId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> logger.debug("removed building to construct in village: {}", saved.getVillageId()));
                });
    }

}
