package pl.app.item.inventory.application.port.in;

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
import pl.app.item.inventory.application.domain.Inventory;
import pl.app.item.inventory.application.domain.InventoryEvent;
import pl.app.item.inventory.application.domain.InventoryException;
import pl.app.item.item.application.domain.CrownPackItem;
import pl.app.item.item.application.domain.Item;
import pl.app.item.item.application.domain.ResourcePackItem;
import pl.app.item.item.application.domain.UnitePackItem;
import pl.app.money.money.application.domain.Money;
import pl.app.money.player_money.application.port.in.PlayerMoneyCommand;
import pl.app.money.player_money.application.port.in.PlayerMoneyService;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.resource.application.domain.ResourceType;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import reactor.core.publisher.Mono;

import java.util.Map;


@Service
@RequiredArgsConstructor
class InventoryServiceImpl implements InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final InventoryDomainRepository inventoryDomainRepository;

    private final PlayerMoneyService playerMoneyService;
    private final VillageResourceService villageResourceService;
    private final VillageArmyService villageArmyService;

    @Override
    public Mono<Inventory> create(InventoryCommand.CreateInventoryCommand command) {
        logger.debug("crating player inventory: {}", command.getPlayerId());
        return mongoTemplate.exists(Query.query(Criteria.where("playerId").is(command.getPlayerId().toHexString())), Inventory.class)
                .flatMap(exist -> exist ? Mono.error(InventoryException.DuplicatedPlayerIdException.fromId(command.getPlayerId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating player inventory: {}, exception: {}", command.getPlayerId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new Inventory(command.getPlayerId());
                    var event = new InventoryEvent.PlayerInventoryCreatedEvent(domain.getPlayerId());
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getPlayerInventoryCreated().getName(), saved.getPlayerId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("created player inventory: {}", saved.getPlayerId()));
                }));
    }

    @Override
    public Mono<Inventory> add(InventoryCommand.AddItemCommand command) {
        logger.debug("adding item to player inventory: {}", command.getPlayerId());
        return inventoryDomainRepository.fetchByPlayerId(command.getPlayerId())
                .doOnError(e -> logger.error("exception occurred while adding item to player inventory: {}, exception: {}", command.getPlayerId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.addItem(command.getItem(), command.getAmount());
                    return mongoTemplate.save(domain)
                            .doOnSuccess(saved -> logger.debug("added item to player inventory: {}", saved.getPlayerId()));
                });
    }

    @Override
    public Mono<Inventory> remove(InventoryCommand.RemoveItemCommand command) {
        logger.debug("removing item in player inventory: {}", command.getPlayerId());
        return inventoryDomainRepository.fetchByPlayerId(command.getPlayerId())
                .doOnError(e -> logger.error("exception occurred while removing item in player inventory: {}, exception: {}", command.getPlayerId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.removeItem(command.getItem(), command.getAmount());
                    return mongoTemplate.save(domain)
                            .doOnSuccess(saved -> logger.debug("removed item in player inventory: {}", saved.getPlayerId()));
                });
    }

    @Override
    public Mono<Inventory> use(InventoryCommand.UseItemCommand command) {
        logger.debug("using item in player inventory: {}", command.getPlayerId());
        return inventoryDomainRepository.fetchByPlayerId(command.getPlayerId())
                .doOnError(e -> logger.error("exception occurred while using item in player inventory: {}, exception: {}", command.getPlayerId(), e.getMessage()))
                .flatMap(domain -> {
                    Item item = command.getItem();
                    domain.removeItem(item, command.getAmount());

                    Mono<?> processItemPublisher = switch (item.getItemType()) {
                        case CROWN_PACK -> {
                            if (item instanceof CrownPackItem crownPackItem) {
                                yield playerMoneyService.add(new PlayerMoneyCommand.AddMoneyCommand(command.getPlayerId(), new Money(crownPackItem.getValue())));
                            }
                            throw new InventoryException.CanNotUseItemException();
                        }
                        case RESOURCE_PACK -> {
                            if (item instanceof ResourcePackItem resourcePackItem) {
                                yield villageResourceService.add(new VillageResourceCommand.AddResourceCommand(command.getDomainObjectId(),
                                        Resource.of(resourcePackItem.getValue(), resourcePackItem.getResourceType()))
                                );
                            }
                            throw new InventoryException.CanNotUseItemException();
                        }
                        case RESOURCE_BUFF -> throw new RuntimeException("not implemented yet"); // TODO
                        case UNIT_PACK -> {
                            if (item instanceof UnitePackItem unitePackItem) {
                                yield villageResourceService.subtract(new VillageResourceCommand.SubtractResourceCommand(command.getDomainObjectId(),
                                                Resource.of(unitePackItem.getValue(), ResourceType.PROVISION))
                                        )
                                        .flatMap(unused -> villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(command.getDomainObjectId(),
                                                Army.of(Map.of(unitePackItem.getUnitType(), unitePackItem.getValue()))
                                        )));
                            }
                            throw new InventoryException.CanNotUseItemException();
                        }
                        case OFFICER -> Mono.empty();
                    };
                    return
                            Mono.when(processItemPublisher)
                                    .then(mongoTemplate.save(domain))
                                    .doOnSuccess(saved -> logger.debug("used item in player inventory: {}", saved.getPlayerId()));
                });
    }
}
