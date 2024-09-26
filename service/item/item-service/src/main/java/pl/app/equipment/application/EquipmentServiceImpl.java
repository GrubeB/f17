package pl.app.equipment.application;

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
import pl.app.equipment.application.domain.Equipment;
import pl.app.equipment.application.domain.EquipmentEvent;
import pl.app.equipment.application.domain.EquipmentException;
import pl.app.equipment.application.port.in.EquipmentCommand;
import pl.app.equipment.application.port.in.EquipmentDomainRepository;
import pl.app.equipment.application.port.in.EquipmentService;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.application.port.in.GearDomainRepository;
import pl.app.item.application.domain.Item;
import pl.app.item.application.port.in.ItemDomainRepository;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;


@Service
@RequiredArgsConstructor
class EquipmentServiceImpl implements EquipmentService {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final EquipmentDomainRepository domainRepository;
    private final ItemDomainRepository itemDomainRepository;
    private final GearDomainRepository gearDomainRepository;

    @Override
    public Mono<Equipment> createEquipment(EquipmentCommand.CreateEquipmentCommand command) {
        logger.debug("creating god equipment, for god: {}", command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("godId").is(command.getGodId())), Equipment.class)
                .flatMap(exist -> exist ? Mono.error(EquipmentException.DuplicatedGodEquipmentException.fromId(command.getGodId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god equipment, for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    Equipment equipment = new Equipment(command.getGodId(), new LinkedHashSet<>());
                    var event = new EquipmentEvent.EquipmentCreatedEvent(
                            equipment.getId(),
                            equipment.getGodId()
                    );
                    return mongoTemplate.insert(equipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getEquipmentCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god equipment: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<Equipment> addCharacterGearToEquipment(EquipmentCommand.AddCharacterGearToEquipmentCommand command) {
        logger.debug("adding character {} gear to equipment of god: {}", command.getCharacterId(), command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .zipWith(gearDomainRepository.fetchByDomainObject(command.getCharacterId(), Gear.LootDomainObjectType.CHARACTER))
                .doOnError(e -> logger.error("exception occurred while adding character {} gear to equipment of god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(t -> {
                    Equipment domain = t.getT1();
                    Gear characterGear = t.getT2();
                    domain.addCharacterGear(characterGear);
                    var event = new EquipmentEvent.CharacterGearAddedToEquipmentEvent(
                            domain.getId(),
                            domain.getGodId(),
                            characterGear.getId(),
                            characterGear.getDomainObjectId()
                    );
                    return mongoTemplate.save(characterGear)
                            .flatMap(unused -> mongoTemplate.save(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterGearAddedToEquipment().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added character {} gear to equipment: {}, of god: {}", characterGear.getDomainObjectId(), saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Equipment> removeCharacterGearFromEquipment(EquipmentCommand.RemoveCharacterGearFromEquipmentCommand command) {
        logger.debug("removing character {} gear from equipment of god: {}", command.getCharacterId(), command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing character {} gear from equipment of god: {}, exception: {}", command.getCharacterId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    Gear characterGear = domain.removeCharacterGearByCharacterId(command.getCharacterId());
                    var event = new EquipmentEvent.CharacterGearRemovedFromEquipmentEvent(
                            domain.getId(),
                            domain.getGodId(),
                            characterGear.getId(),
                            characterGear.getDomainObjectId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCharacterGearRemovedFromEquipment().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed character {} gear from equipment: {}, of god: {}", characterGear.getDomainObjectId(), saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Equipment> addItemToEquipment(EquipmentCommand.AddItemToEquipmentCommand command) {
        logger.debug("adding item to equipment for god: {}", command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while adding item to equipment for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .zipWith(itemDomainRepository.fetchById(command.getItemId()))
                .flatMap(tuple2 -> {
                    Equipment domain = tuple2.getT1();
                    Item item = tuple2.getT2();
                    domain.addItem(item);
                    var event = new EquipmentEvent.EquipmentItemAddedEvent(
                            domain.getId(),
                            domain.getGodId(),
                            item.getId(),
                            item.getType().name()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added item to equipment: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Equipment> removeItemFromEquipment(EquipmentCommand.RemoveItemFromEquipmentCommand command) {
        logger.debug("removing item from equipment for god: {}", command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while removing item from equipment for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    Item item = domain.removeItemById(command.getItemId());
                    var event = new EquipmentEvent.EquipmentItemRemovedEvent(
                            domain.getId(),
                            domain.getGodId(),
                            item.getId(),
                            item.getType().name()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed item from equipment: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
