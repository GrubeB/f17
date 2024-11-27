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
import pl.app.building.building.service.BuildingLevelService;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureDomainRepository;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
class BuilderServiceImpl implements BuilderService {
    private static final Logger logger = LoggerFactory.getLogger(BuilderServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final BuilderDomainRepository builderDomainRepository;
    private final VillageInfrastructureDomainRepository villageInfrastructureDomainRepository;
    private final BuildingLevelService buildingLevelDomainRepository;
    private final VillageResourceService villageResourceService;

    @Override
    public Mono<Builder> crate(BuilderCommand.CreateBuilderCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), Builder.class)
                        .flatMap(exist -> exist ? Mono.error(BuilderException.DuplicatedBuilderException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new Builder(command.getVillageId(), 7);
                            var event = new BuilderEvent.BuilderCreatedEvent(domain.getVillageId());
                            return mongoTemplate.insert(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getBuilderCreated().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating builder for village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created builder for village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while crating builder for village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<Builder> add(BuilderCommand.AddBuildingToConstructCommand command) {
        return Mono.fromCallable(() ->
                Mono.zip(
                                builderDomainRepository.fetchByVillageId(command.getVillageId()),
                                villageInfrastructureDomainRepository.fetchByVillageId(command.getVillageId()),
                                buildingLevelDomainRepository.fetchAll(command.getType())
                        )
                        .flatMap(t -> {
                            var domain = t.getT1();
                            var infrastructure = t.getT2();
                            var buildingLevels = t.getT3();
                            var construct = domain.addConstruct(command.getType(), infrastructure.getBuildings(), buildingLevels);

                            var event = new BuilderEvent.ConstructAddedEvent(domain.getVillageId(), construct.getType(), construct.getToLevel(), construct.getFrom(), construct.getTo());
                            return villageResourceService.subtract(new VillageResourceCommand.SubtractResourceCommand(command.getVillageId(), construct.getCost()))
                                    .then(mongoTemplate.save(domain))
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getConstructAdded().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("adding construction in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("added construction in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while adding construction in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<Builder> start(BuilderCommand.StartBuildingToConstructCommand command) {
        return Mono.fromCallable(() ->
                builderDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            var construct = domain.starFirstConstruct();
                            var event = new BuilderEvent.ConstructStartedEvent(domain.getVillageId(), construct.getType(), construct.getToLevel(), construct.getFrom(), construct.getTo());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getConstructStarted().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("starting construction in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("started construction village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while starting construction in village: {}, exception: {}", command.getVillageId(), e.toString())
        ).onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<Builder> finish(BuilderCommand.FinishBuildingConstructCommand command) {
        return Mono.fromCallable(() ->
                builderDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            var construct = domain.finishFirstConstruct();
                            if (construct.isEmpty()) {
                                return Mono.just(domain);
                            }
                            var event = new BuilderEvent.ConstructFinishedEvent(domain.getVillageId(), construct.get().getType(), construct.get().getToLevel(), construct.get().getFrom(), construct.get().getTo());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getConstructFinished().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("finishing construction in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("finished construction in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while finishing construction in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<Builder> cancel(BuilderCommand.CancelBuildingConstructCommand command) {
        return Mono.fromCallable(() ->
                builderDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            var canceledConstructions = domain.cancelConstruct(command.getType(), command.getToLevel());
                            if (canceledConstructions.isEmpty()) {
                                return Mono.just(domain);
                            }
                            return Flux.fromIterable(canceledConstructions)
                                    .flatMap(construct -> villageResourceService.add(new VillageResourceCommand.AddResourceCommand(command.getVillageId(), construct.getCost())).thenReturn(construct))
                                    .map(construct -> new BuilderEvent.ConstructCanceledEvent(domain.getVillageId(), construct.getType(), construct.getToLevel(), construct.getFrom(), construct.getTo()))
                                    .flatMap(event -> Mono.fromFuture(kafkaTemplate.send(topicNames.getConstructCanceled().getName(), domain.getVillageId(), event)))
                                    .then(mongoTemplate.save(domain));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("canceling construction in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("canceled construction in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while canceling construction in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<Builder> reject(BuilderCommand.RejectBuildingConstructCommand command) {
        return Mono.fromCallable(() ->
                builderDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            var rejectedConstructions = domain.rejectConstruct(command.getType());
                            if (rejectedConstructions.isEmpty()) {
                                return Mono.just(domain);
                            }
                            return Flux.fromIterable(rejectedConstructions)
                                    .flatMap(construct -> villageResourceService.add(new VillageResourceCommand.AddResourceCommand(command.getVillageId(), construct.getCost())).thenReturn(construct))
                                    .map(construct -> new BuilderEvent.ConstructRejectedEvent(domain.getVillageId(), construct.getType(), construct.getToLevel(), construct.getFrom(), construct.getTo()))
                                    .flatMap(event -> Mono.fromFuture(kafkaTemplate.send(topicNames.getConstructRejected().getName(), domain.getVillageId(), event)))
                                    .then(mongoTemplate.save(domain));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("rejecting construction in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("rejected construction in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while rejecting construction in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }
}
