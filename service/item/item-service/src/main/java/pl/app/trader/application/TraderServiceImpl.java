package pl.app.trader.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.common.shared.model.ItemType;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.god_equipment.application.port.in.GodEquipmentCommand;
import pl.app.god_equipment.application.port.in.GodEquipmentService;
import pl.app.item.application.domain.Item;
import pl.app.item.application.port.in.ItemDomainRepository;
import pl.app.trader.application.domain.Trader;
import pl.app.trader.application.domain.TraderEvent;
import pl.app.trader.application.domain.TraderException;
import pl.app.trader.application.port.in.TraderCommand;
import pl.app.trader.application.port.in.TraderService;
import pl.app.trader.application.port.out.GodMoneyService;
import pl.app.trader.application.port.out.ItemGenerator;
import pl.app.trader.application.port.in.TraderDomainRepository;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
class TraderServiceImpl implements TraderService {
    private static final Logger logger = LoggerFactory.getLogger(TraderServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;
    private final ItemGenerator itemTemplateDomainRepository;
    private final ItemDomainRepository itemDomainRepository;
    private final TraderDomainRepository traderDomainRepository;
    private final GodMoneyService godMoneyService;
    private final GodEquipmentService godEquipmentService;

    @Override
    public Mono<Trader> create(TraderCommand.CrateTraderCommand command) {
        logger.debug("creating trader for god: {}", command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("godId").is(command.getGodId())), Trader.class)
                .flatMap(exist -> exist ? Mono.error(TraderException.DuplicatedGodException.fromId(command.getGodId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating trader for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .then(itemTemplateDomainRepository.createRandomItems(9, ItemType.geTypes(), 1).collect(Collectors.toSet()))
                .flatMap(randomItems -> {
                    Trader domain = new Trader(command.getGodId(), randomItems);
                    var event = new TraderEvent.TraderCreatedEvent(
                            domain.getId(), domain.getGodId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getTraderCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created trader: {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Trader> renew(TraderCommand.RenewItemsCommand command) {
        logger.debug("renew trader items for god: {}", command.getGodId());
        return traderDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while renew trader items for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                // TODO get level from god character
                .zipWith(itemTemplateDomainRepository.createRandomItems(9, ItemType.geTypes(), 10).collect(Collectors.toSet()))
                .flatMap(tuple2 -> {
                    Trader domain = tuple2.getT1();
                    Set<Item> items = tuple2.getT2();
                    domain.renewItems(items);
                    var event = new TraderEvent.TraderItemsRenewedEvent(
                            domain.getId(), domain.getGodId()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getTraderItemsRenewed().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("renewed trader items : {}, for god: {}", saved.getId(), saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Trader> buy(TraderCommand.BuyItemCommand command) {
        logger.debug("buying item for god: {}", command.getGodId());
        return traderDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while buying item for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    Item item = domain.takeItem(command.getItemId());
                    return godMoneyService.subtractMoney(domain.getGodId(), item.getMoney())
                            .then(godEquipmentService.addItemToEquipment(new GodEquipmentCommand.AddItemToGodEquipmentCommand(command.getGodId(), item.getId())))
                            .then(Mono.defer(() -> {
                                var event = new TraderEvent.GodBoughtItemEvent(
                                        domain.getId(), domain.getGodId(), item.getId(), item.getMoney()
                                );
                                return mongoTemplate.save(domain)
                                        .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodBoughtItem().getName(), saved.getId(), event)).thenReturn(saved))
                                        .doOnSuccess(saved -> {
                                            logger.debug("god {} bought item {} ", saved.getGodId(), command.getItemId());
                                            logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                                        });
                            }));
                });
    }

    @Override
    public Mono<Trader> sell(TraderCommand.SellItemCommand command) {
        logger.debug("selling item {} of from: {}",command.getItemId(), command.getGodId());
        return traderDomainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while selling item {} from god: {}, exception: {}",command.getItemId(), command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    return itemDomainRepository.fetchById(command.getItemId())
                            .flatMap(item -> godEquipmentService.removeItemFromEquipment(new GodEquipmentCommand.RemoveItemFromGodEquipmentCommand(command.getGodId(), command.getItemId())).thenReturn(item))
                            .flatMap(item ->{
                                var event = new TraderEvent.GodSoldItemEvent(
                                        domain.getId(), domain.getGodId(), command.getItemId(), item.getMoney()
                                );
                                return mongoTemplate.save(domain)
                                        .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodSoldItem().getName(), saved.getId(), event)).thenReturn(saved))
                                        .doOnSuccess(saved -> {
                                            logger.debug("god {} sold item {} ", saved.getGodId(), command.getItemId());
                                            logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                                        });
                            });
                });
    }
}
