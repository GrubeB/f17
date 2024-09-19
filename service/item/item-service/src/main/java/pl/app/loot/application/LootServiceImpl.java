package pl.app.loot.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.common.shared.model.Money;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.item_template.application.port.in.ItemTemplateDomainRepository;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.aplication.domain.LootEvent;
import pl.app.loot.aplication.domain.LootException;
import pl.app.loot.aplication.domain.LootItem;
import pl.app.loot.application.port.in.LootCommand;
import pl.app.loot.application.port.in.LootDomainRepository;
import pl.app.loot.application.port.in.LootService;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;


@Service
@RequiredArgsConstructor
class LootServiceImpl implements LootService {
    private static final Logger logger = LoggerFactory.getLogger(LootServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final LootDomainRepository lootDomainRepository;
    private final ItemTemplateDomainRepository itemTemplateDomainRepository;

    @Override
    public Mono<Loot> crate(LootCommand.CreateLootCommand command) {
        logger.debug("creating loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return mongoTemplate.exists(Query.query(Criteria.where("domainObjectId").is(command.getDomainObjectId())
                        .and("domainObjectType").is(command.getDomainObjectType())), Loot.class)
                .flatMap(exist -> exist ? Mono.error(LootException.DuplicatedLootException.fromId(command.getDomainObjectId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating loot for: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new Loot(command.getDomainObjectId(), command.getDomainObjectType(), new LinkedHashSet<>(), new Money());
                    var event = new LootEvent.LootCreatedEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getLootCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<Loot> remove(LootCommand.RemoveLootCommand command) {
        logger.debug("removing loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return lootDomainRepository.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while removing loot for: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .flatMap(domain -> {
                    var event = new LootEvent.LootRemovedEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType()
                    );
                    return mongoTemplate.remove(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getLootRemoved().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("removed loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Loot> setItem(LootCommand.SetItemCommand command) {
        logger.debug("setting item to loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return lootDomainRepository.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while setting item to loot for: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .zipWith(itemTemplateDomainRepository.fetchTemplateById(command.getItemTemplateId()))
                .flatMap(t -> {
                    var domain = t.getT1();
                    var itemTemplate = t.getT2();
                    var lootItem = new LootItem(itemTemplate, command.getChance(), command.getAmount());
                    domain.addItem(lootItem);
                    var event = new LootEvent.LootItemSetEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType(),
                            lootItem.getItemTemplate().getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getLootItemSet().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("set item to loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Loot> removeItem(LootCommand.RemoveItemCommand command) {
        logger.debug("removing item to loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return lootDomainRepository.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while removing item to loot for: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .flatMap(domain -> {
                    LootItem lootItem = domain.removeItem(command.getItemTemplateId());
                    var event = new LootEvent.LootItemRemovedEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType(),
                            lootItem.getItemTemplate().getId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getLootItemRemoved().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("removed item to loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Loot> setMoney(LootCommand.SetMoneyCommand command) {
        logger.debug("setting money to loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
        return lootDomainRepository.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while setting money to loot for: {} - {}, exception: {}", command.getDomainObjectType(), command.getDomainObjectId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.setMoney(command.getMoney());
                    var event = new LootEvent.LootMoneySetEvent(
                            domain.getId(),
                            domain.getDomainObjectId(),
                            domain.getDomainObjectType()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(unused -> Mono.fromFuture(kafkaTemplate.send(topicNames.getLootMoneySet().getName(), domain.getId(), event)).thenReturn(domain))
                            .doOnSuccess(saved -> {
                                logger.debug("set money to loot for: {} - {}", command.getDomainObjectType(), command.getDomainObjectId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
