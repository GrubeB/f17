package pl.app.inventory.inventory.application.port.in;

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
import pl.app.inventory.inventory.application.domain.Inventory;
import pl.app.inventory.inventory.application.domain.InventoryEvent;
import pl.app.inventory.inventory.application.domain.InventoryException;
import pl.app.inventory.shared.*;
import pl.app.money.share.Money;
import pl.app.money.player_money.application.port.in.PlayerMoneyCommand;
import pl.app.money.player_money.application.port.in.PlayerMoneyService;
import pl.app.resource.share.Resource;
import pl.app.resource.share.ResourceType;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import pl.app.village.village_effect.application.domain.EffectType;
import pl.app.village.village_effect.application.port.in.VillageEffectCommand;
import pl.app.village.village_effect.application.port.in.VillageEffectService;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;


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
    private final VillageEffectService villageEffectService;

    @Override
    public Mono<Inventory> create(InventoryCommand.CreateInventoryCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("playerId").is(command.getPlayerId().toHexString())), Inventory.class)
                        .flatMap(exist -> exist ? Mono.error(InventoryException.DuplicatedPlayerIdException.fromId(command.getPlayerId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new Inventory(command.getPlayerId());
                            var event = new InventoryEvent.PlayerInventoryCreatedEvent(domain.getPlayerId());
                            return mongoTemplate.insert(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getPlayerInventoryCreated().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating inventory for player: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created inventory for player: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while crating inventory for player: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }

    @Override
    public Mono<Inventory> add(InventoryCommand.AddItemCommand command) {
        return Mono.fromCallable(() ->
                inventoryDomainRepository.fetchByPlayerId(command.getPlayerId())
                        .flatMap(domain -> {
                            domain.addItem(command.getItem(), command.getAmount());
                            var event = new InventoryEvent.ItemAddedEvent(domain.getPlayerId(), command.getItem(), command.getAmount());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getItemAdded().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("adding item to player inventory: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("added item to player inventory: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while adding item to player inventory: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }

    @Override
    public Mono<Inventory> remove(InventoryCommand.RemoveItemCommand command) {
        return Mono.fromCallable(() ->
                inventoryDomainRepository.fetchByPlayerId(command.getPlayerId())
                        .flatMap(domain -> {
                            domain.removeItem(command.getItem(), command.getAmount());
                            var event = new InventoryEvent.ItemRemovedEvent(domain.getPlayerId(), command.getItem(), command.getAmount());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getItemRemoved().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("removing item in player inventory: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed item in player inventory: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while removing item in player inventory: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }

    @Override
    public Mono<Inventory> use(InventoryCommand.UseItemCommand command) {
        return Mono.fromCallable(() ->
                inventoryDomainRepository.fetchByPlayerId(command.getPlayerId())
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
                                case RESOURCE_BUFF -> {
                                    if (item instanceof ResourceBuffItem resourceBuffItem) {
                                        EffectType effectType = switch (resourceBuffItem.getResourceType()) {
                                            case WOOD -> EffectType.WOOD_BUFF;
                                            case CLAY -> EffectType.CLAY_BUFF;
                                            case IRON -> EffectType.IRON_BUFF;
                                            default -> throw new InventoryException.CanNotUseItemException();
                                        };
                                        yield villageEffectService.add(new VillageEffectCommand.AddEffectCommand(command.getDomainObjectId(), effectType,
                                                resourceBuffItem.getDuration(), resourceBuffItem.getValue()));
                                    }
                                    throw new InventoryException.CanNotUseItemException();
                                }
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

                            var event = new InventoryEvent.ItemUsedEvent(domain.getPlayerId(), command.getItem(), command.getAmount(), command.getDomainObjectId());
                            return Mono.when(processItemPublisher)
                                    .then(mongoTemplate.save(domain))
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getItemUsed().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("using item in player inventory: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("used item in player inventory: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while using item in player inventory: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }
}
