package pl.app.account_equipment.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.account_equipment.application.domain.AccountEquipment;
import pl.app.account_equipment.application.domain.AccountEquipmentEvent;
import pl.app.account_equipment.application.domain.AccountEquipmentException;
import pl.app.account_equipment.application.port.in.AccountEquipmentCommand;
import pl.app.account_equipment.application.port.in.AccountEquipmentService;
import pl.app.account_equipment.application.port.out.AccountEquipmentDomainRepository;
import pl.app.account_equipment.application.port.out.ItemDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.item.application.domain.Item;
import pl.app.item.application.domain.ItemEvent;
import pl.app.item.application.domain.Outfit;
import pl.app.item.application.domain.Weapon;
import pl.app.item.application.port.in.ItemCommand;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;


@Service
@RequiredArgsConstructor
class AccountEquipmentServiceImpl implements AccountEquipmentService {
    private static final Logger logger = LoggerFactory.getLogger(AccountEquipmentServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final AccountEquipmentDomainRepository domainRepository;
    private final ItemDomainRepository itemDomainRepository;

    @Override
    public Mono<AccountEquipment> createAccountEquipment(AccountEquipmentCommand.CreateAccountEquipmentCommand command) {
        logger.debug("creating account equipment, for account: {}", command.getAccountId());
        return mongoTemplate.exists(Query.query(Criteria.where("accountId").is(command.getAccountId())), AccountEquipment.class)
                .flatMap(exist -> exist ? Mono.error(AccountEquipmentException.DuplicatedAccountException.fromId(command.getAccountId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating account equipment, for account: {}, exception: {}", command.getAccountId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    AccountEquipment accountEquipment = new AccountEquipment(command.getAccountId(), new LinkedHashSet<>());
                    var event = new AccountEquipmentEvent.AccountEquipmentCreatedEvent(
                            accountEquipment.getId(),
                            accountEquipment.getAccountId()
                    );
                    return mongoTemplate.insert(accountEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getAccountEquipmentCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("creating account equipment: {}, for account: {}", saved.getId(), saved.getAccountId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<AccountEquipment> addItemToAccountEquipment(AccountEquipmentCommand.AddItemToAccountEquipmentCommand command) {
        logger.debug("adding item to equipment for account: {}", command.getAccountId());
        return domainRepository.fetchByAccountId(command.getAccountId())
                .doOnError(e -> logger.error("exception occurred while adding item to equipment for account: {}, exception: {}", command.getAccountId(), e.getMessage()))
                .zipWith(itemDomainRepository.fetchById(command.getItemId(), command.getItemType()))
                .flatMap(tuple2 -> {
                    AccountEquipment accountEquipment = tuple2.getT1();
                    Item item = tuple2.getT2();
                    accountEquipment.addItem(item);
                    var event = new AccountEquipmentEvent.AccountEquipmentItemAddedEvent(
                            accountEquipment.getId(),
                            accountEquipment.getAccountId(),
                            item.getId(),
                            item.getType().name()
                    );
                    return mongoTemplate.save(accountEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getAccountEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added item to equipment: {}, for account: {}", saved.getId(), saved.getAccountId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<AccountEquipment> removeItemToAccountEquipment(AccountEquipmentCommand.RemoveItemToAccountEquipmentCommand command) {
        logger.debug("removing item from equipment for account: {}", command.getAccountId());
        return domainRepository.fetchByAccountId(command.getAccountId())
                .doOnError(e -> logger.error("exception occurred while removing item from equipment for account: {}, exception: {}", command.getAccountId(), e.getMessage()))
                .flatMap(accountEquipment -> {
                    Item item = accountEquipment.removeItemById(command.getItemId(), command.getItemType());
                    var event = new AccountEquipmentEvent.AccountEquipmentItemRemovedEvent(
                            accountEquipment.getId(),
                            accountEquipment.getAccountId(),
                            item.getId(),
                            item.getType().name()
                    );
                    return mongoTemplate.save(accountEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getAccountEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed item from equipment: {}, for account: {}", saved.getId(), saved.getAccountId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<AccountEquipment> setCharacterItem(AccountEquipmentCommand.SetCharacterItemCommand command) {
        logger.debug("setting item to character: {}, for account: {}", command.getCharacterId(), command.getAccountId());
        return domainRepository.fetchByAccountId(command.getAccountId())
                .doOnError(e -> logger.error("exception occurred while setting item to character: {}, for account: {}, exception: {}",  command.getCharacterId(), command.getAccountId(), e.getMessage()))
                .flatMap(accountEquipment -> {
                    Item item = accountEquipment.setItemToCharacterGear(command.getCharacterId(), command.getSlot(), command.getItemId(), command.getItemType());
                    var event = new AccountEquipmentEvent.CharacterItemSetEvent(
                            accountEquipment.getId(),
                            accountEquipment.getAccountId(),
                            item.getId(),
                            item.getType().name(),
                            command.getCharacterId()
                    );
                    return mongoTemplate.save(accountEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getAccountEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("set item to character: {}, for account: {}", command.getCharacterId(), saved.getAccountId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<AccountEquipment> removeCharacterItem(AccountEquipmentCommand.RemoveCharacterItemCommand command) {
        logger.debug("removing item from character: {}, for account: {}", command.getCharacterId(), command.getAccountId());
        return domainRepository.fetchByAccountId(command.getAccountId())
                .doOnError(e -> logger.error("exception occurred while removing item from character: {}, for account: {}, exception: {}",  command.getCharacterId(), command.getAccountId(), e.getMessage()))
                .flatMap(accountEquipment -> {
                    Item item = accountEquipment.removeItemFromCharacterGear(command.getCharacterId(), command.getSlot());
                    var event = new AccountEquipmentEvent.CharacterItemRemovedEvent(
                            accountEquipment.getId(),
                            accountEquipment.getAccountId(),
                            item.getId(),
                            item.getType().name(),
                            command.getCharacterId()
                    );
                    return mongoTemplate.save(accountEquipment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getAccountEquipmentItemAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("removed item from character: {}, for account: {}", command.getCharacterId(), saved.getAccountId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

}
