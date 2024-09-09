package pl.app.gear.application;

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
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.aplication.domain.GearEvent;
import pl.app.gear.aplication.domain.GearException;
import pl.app.gear.application.port.in.GearCommand;
import pl.app.gear.application.port.in.GearDomainRepository;
import pl.app.gear.application.port.in.GearService;
import pl.app.item.application.port.in.ItemDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class GearServiceImpl implements GearService {
    private static final Logger logger = LoggerFactory.getLogger(GearServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final GearDomainRepository gearDomainRepository;
    private final ItemDomainRepository itemDomainRepository;


    @Override
    public Mono<Gear> crate(GearCommand.CreateGearCommand command) {
        logger.debug("creating gear: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return mongoTemplate.exists(Query.query(Criteria.where("domainObjectId").is(command.getDomainObjectId())
                        .and("domainObjectType").is(command.getDomainObjectType())), Gear.class)
                .flatMap(exist -> exist ? Mono.error(GearException.DuplicatedGearException.fromId(command.getDomainObjectId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating  gear: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new Gear(command.getDomainObjectId(), command.getDomainObjectType());
                    var event = new GearEvent.GearCreatedEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGearCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created gear: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<Gear> remove(GearCommand.RemoveGearCommand command) {
        logger.debug("removing gear: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return gearDomainRepository.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while removing gear: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new GearEvent.GearRemovedEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGearRemoved().getName(), domain.getId(), event)).thenReturn(unused))
                            .doOnSuccess(unused -> {
                                logger.debug("removed gear: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(domain);
                });
    }

    @Override
    public Mono<Gear> setItem(GearCommand.SetItemCommand command) {
        logger.debug("setting item to gear: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return gearDomainRepository.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while setting item to gear: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .zipWith(itemDomainRepository.fetchById(command.getItemId()))
                .flatMap(t -> {
                    var domain = t.getT1();
                    var item = t.getT2();
                    domain.setItem(item, command.getSlot());
                    var event = new GearEvent.ItemSetEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType(),
                            item.getId(),
                            command.getSlot()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGearItemSet().getName(), domain.getId(), event)).thenReturn(unused))
                            .doOnSuccess(unused -> {
                                logger.debug("set item {} for gear: {} - {}", item.getId(), command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(domain);
                });
    }

    @Override
    public Mono<Gear> removeItem(GearCommand.RemoveItemCommand command) {
        logger.debug("removing item from gear: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return gearDomainRepository.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while removing item from gear: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .flatMap(domain -> {
                    var item = domain.removeItem(command.getSlot());
                    var event = new GearEvent.ItemRemovedEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType(),
                            item.getId(),
                            command.getSlot()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGearItemRemoved().getName(), domain.getId(), event)).thenReturn(unused))
                            .doOnSuccess(unused -> {
                                logger.debug("removed item {} from gear: {} - {}", item.getId(), command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(domain);
                });
    }
}
