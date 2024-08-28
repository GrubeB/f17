package pl.app.god_equipment.application;

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
import pl.app.god_equipment.application.domain.CharacterGear;
import pl.app.god_equipment.application.domain.GodEquipment;
import pl.app.god_equipment.application.domain.GodEquipmentEvent;
import pl.app.god_equipment.application.domain.GodEquipmentException;
import pl.app.god_equipment.application.port.in.GodEquipmentCommand;
import pl.app.god_equipment.application.port.in.GodEquipmentService;
import pl.app.god_equipment.application.port.out.GodEquipmentDomainRepository;
import pl.app.god_equipment.application.port.out.ItemDomainRepository;
import pl.app.item.application.domain.Item;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;


@Service
@RequiredArgsConstructor
class GodEquipmentServiceImpl implements GodEquipmentService {
    private static final Logger logger = LoggerFactory.getLogger(GodEquipmentServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final GodEquipmentDomainRepository domainRepository;
    private final ItemDomainRepository itemDomainRepository;

    @Override
    public Mono<GodEquipment> createEquipment(GodEquipmentCommand.CreateGodEquipmentCommand command) {
        logger.debug("creating god equipment, for god: {}", command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("godId").is(command.getGodId())), GodEquipment.class)
                .flatMap(exist -> exist ? Mono.error(GodEquipmentException.DuplicatedGodEquipmentException.fromId(command.getGodId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god equipment, for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    GodEquipment godEquipment = new GodEquipment(command.getGodId(), new LinkedHashSet<>());
                    var event = new GodEquipmentEvent.GodEquipmentCreatedEvent(
                            godEquipment.getId(),
                            godEquipment.getGodId()
                    );
                    return mongoTemplate.insert(godEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodEquipmentCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god equipment: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<GodEquipment> addCharacterGearToGodEquipment(GodEquipmentCommand.AddCharacterGearToGodEquipmentCommand command) {
        logger.debug("adding character {} gear to equipment of god: {}", command.getCharacterId(), command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .zipWith(domainRepository.fetchCharacterGearByCharacterId(command.getCharacterId()))
                .doOnError(e -> logger.error("exception occurred while adding character {} gear to equipment of god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(t -> {
                    GodEquipment domain = t.getT1();
                    CharacterGear characterGear = t.getT2();
                    domain.addCharacterGear(characterGear);
                    var event = new GodEquipmentEvent.CharacterGearAddedToGodEquipmentEvent(
                            domain.getId(),
                            domain.getGodId(),
                            characterGear.getId(),
                            characterGear.getCharacterId()
                    );
                    return mongoTemplate.save(characterGear)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterGearAddedToGodEquipment().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added character {} gear to equipment: {}, of god: {}", characterGear.getCharacterId(), saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodEquipment> removeCharacterGearFromGodEquipment(GodEquipmentCommand.RemoveCharacterGearFromGodEquipmentCommand command) {
        logger.debug("removing character {} gear from equipment of god: {}", command.getCharacterId(), command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing character {} gear from equipment of god: {}, exception: {}" , command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    CharacterGear characterGear= domain.removeCharacterGearByCharacterId(command.getCharacterId());
                    var event = new GodEquipmentEvent.CharacterGearRemovedFromGodEquipmentEvent(
                            domain.getId(),
                            domain.getGodId(),
                            characterGear.getId(),
                            characterGear.getCharacterId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterGearRemovedFromGodEquipment().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed character {} gear from equipment: {}, of god: {}", characterGear.getCharacterId(), saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodEquipment> addItemToEquipment(GodEquipmentCommand.AddItemToGodEquipmentCommand command) {
        logger.debug("adding item to equipment for god: {}", command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while adding item to equipment for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .zipWith(itemDomainRepository.fetchById(command.getItemId()))
                .flatMap(tuple2 -> {
                    GodEquipment godEquipment = tuple2.getT1();
                    Item item = tuple2.getT2();
                    godEquipment.addItem(item);
                    var event = new GodEquipmentEvent.GodEquipmentItemAddedEvent(
                            godEquipment.getId(),
                            godEquipment.getGodId(),
                            item.getId(),
                            item.getType().name()
                    );
                    return mongoTemplate.save(godEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added item to equipment: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodEquipment> removeItemFromEquipment(GodEquipmentCommand.RemoveItemFromGodEquipmentCommand command) {
        logger.debug("removing item from equipment for god: {}", command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing item from equipment for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(godEquipment -> {
                    Item item = godEquipment.removeItemById(command.getItemId());
                    var event = new GodEquipmentEvent.GodEquipmentItemRemovedEvent(
                            godEquipment.getId(),
                            godEquipment.getGodId(),
                            item.getId(),
                            item.getType().name()
                    );
                    return mongoTemplate.save(godEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed item from equipment: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodEquipment> setCharacterItem(GodEquipmentCommand.SetCharacterItemCommand command) {
        logger.debug("setting item to character: {}, for god: {}", command.getCharacterId(), command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while setting item to character: {}, for god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    CharacterGear characterGear = domain.getCharacterGearByIdOrThrow(command.getCharacterId());
                    Item item = domain.setItemToCharacterGear(command.getCharacterId(), command.getSlot(), command.getItemId());
                    var event = new GodEquipmentEvent.CharacterItemSetEvent(
                            domain.getId(),
                            domain.getGodId(),
                            item.getId(),
                            item.getType().name(),
                            command.getCharacterId()
                    );
                    return mongoTemplate.save(characterGear)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("set item to character: {}, for god: {}", command.getCharacterId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<GodEquipment> removeCharacterItem(GodEquipmentCommand.RemoveCharacterItemCommand command) {
        logger.debug("removing item from character: {}, for god: {}", command.getCharacterId(), command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing item from character: {}, for god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    CharacterGear characterGear = domain.getCharacterGearByIdOrThrow(command.getCharacterId());
                    Item item = domain.removeItemFromCharacterGear(command.getCharacterId(), command.getSlot());
                    var event = new GodEquipmentEvent.CharacterItemRemovedEvent(
                            domain.getId(),
                            domain.getGodId(),
                            item.getId(),
                            item.getType().name(),
                            command.getCharacterId()
                    );
                    return mongoTemplate.save(characterGear)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed item from character: {}, for god: {}", command.getCharacterId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

}
