package pl.app.resource.village_resource.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.application.domain.VillageResourceEvent;
import pl.app.resource.village_resource.application.domain.VillageResourceException;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class VillageResourceServiceImpl implements VillageResourceService {
    private static final Logger logger = LoggerFactory.getLogger(VillageResourceServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final VillageResourceDomainRepository villageResourceDomainRepository;

    @Override
    public Mono<VillageResource> crate(VillageResourceCommand.CreateVillageResourceCommand command) {
        logger.debug("crating village resource: {}", command.getVillageId());
        return mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), VillageResource.class)
                .flatMap(exist -> exist ? Mono.error(VillageResourceException.DuplicatedVillageResourceException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating resource position: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new VillageResource(command.getVillageId(), Resource.zero(), new Resource(1_000, 1_000, 1_000, 1_000));
                    var event = new VillageResourceEvent.VillageResourceCreatedEvent(domain.getVillageId());
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageResourceCreated().getName(), saved.getVillageId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("created village resource: {}", saved.getVillageId()));
                }));
    }

    @Override
    public Mono<VillageResource> add(VillageResourceCommand.AddResourceCommand command) {
        logger.debug("adding resource to village: {}", command.getVillageId());
        return villageResourceDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while adding resource to village: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.addResource(command.getResource());
                    var event = new VillageResourceEvent.ResourceAddedEvent(domain.getVillageId(), command.getResource());
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getResourceAdded().getName(), saved.getVillageId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("added resource: {}", saved.getVillageId()));
                });
    }

    @Override
    public Mono<VillageResource> subtract(VillageResourceCommand.SubtractResourceCommand command) {
        logger.debug("subtracting resource from village: {}", command.getVillageId());
        return villageResourceDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while subtracting resource from village: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.subtractResource(command.getResource());
                    var event = new VillageResourceEvent.ResourceSubtractedEvent(domain.getVillageId(), command.getResource());
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getResourceSubtracted().getName(), saved.getVillageId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("subtracted resource: {}", saved.getVillageId()));
                });
    }

    @Override
    public Mono<VillageResource> change(VillageResourceCommand.ChangeMaxResourceCommand command) {
        logger.debug("changing max resource in village: {}", command.getVillageId());
        return villageResourceDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while changing max resource in village: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.setResourceMax(command.getResourceMax());
                    return mongoTemplate.save(domain)
                            .doOnSuccess(saved -> logger.debug("changed max resource in village: {}", saved.getVillageId()));
                });
    }
}
