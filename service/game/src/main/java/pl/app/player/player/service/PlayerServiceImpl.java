package pl.app.player.player.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinCommand;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinService;
import pl.app.inventory.inventory.application.port.in.InventoryCommand;
import pl.app.inventory.inventory.application.port.in.InventoryService;
import pl.app.money.player_money.application.port.in.PlayerMoneyCommand;
import pl.app.money.player_money.application.port.in.PlayerMoneyService;
import pl.app.player.player.model.Player;
import pl.app.player.player.model.PlayerEvent;
import pl.app.player.player.model.PlayerException;
import pl.app.player.player.service.dto.PlayerCreateDto;
import pl.app.player.player.service.dto.PlayerDto;
import pl.app.player.player.service.dto.PlayerUpdateDto;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
class PlayerServiceImpl implements PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final PlayerMapper playerMapper;
    private final PlayerMoneyService playerMoneyService;
    private final PlayerGoldCoinService playerGoldCoinService;
    private final InventoryService inventoryService;


    @Override
    public Mono<PlayerDto> create(PlayerCreateDto dto) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("accountId").is(dto.getAccountId())), Player.class)
                        .flatMap(exist -> exist ? Mono.error(PlayerException.DuplicatedPlayerAccountIdException.fromAccountId(dto.getAccountId())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var entity = Player.builder()
                                    .playerId(ObjectId.get())
                                    .accountId(dto.getAccountId())
                                    .name(dto.getName())
                                    .description(dto.getDescription())
                                    .build();
                            var event = new PlayerEvent.PlayerCreatedEvent(entity.getPlayerId());
                            return playerMoneyService.crate(new PlayerMoneyCommand.CreatePlayerMoneyCommand(entity.getPlayerId()))
                                    .then(playerGoldCoinService.crate(new PlayerGoldCoinCommand.CreatePlayerGoldCoinCommand(entity.getPlayerId())))
                                    .then(inventoryService.create(new InventoryCommand.CreateInventoryCommand(entity.getPlayerId())))
                                    .then(mongoTemplate.insert(entity))
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getPlayerCreated().getName(), entity.getPlayerId(), event)))
                                    .thenReturn(playerMapper.map(entity, PlayerDto.class));
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating player for account: {}", dto.getAccountId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created player: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while crating player for account: {}, exception: {}", dto.getAccountId(), e.toString())
        );
    }

    @Override
    public Mono<PlayerDto> update(@NonNull ObjectId id, PlayerUpdateDto dto) {
        return Mono.fromCallable(() ->
                mongoTemplate.query(Player.class).matching(Query.query(Criteria.where("_id").is(id))).one()
                        .flatMap(entity -> {
                            entity.setName(dto.getName());
                            entity.setDescription(dto.getDescription());
                            return mongoTemplate.save(entity)
                                    .thenReturn(playerMapper.map(entity, PlayerDto.class));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("updating player: {}", id)
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("updated player: {}", id)
        ).doOnError(e ->
                logger.error("exception occurred while updating player: {}, exception: {}", id, e.toString())
        );
    }

    @Override
    public Mono<Void> deleteById(@NonNull ObjectId id) {
        return Mono.fromCallable(() ->
                mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), Player.class)
                        .then()
        ).doOnSubscribe(subscription ->
                logger.debug("deleting player: {}", id)
        ).flatMap(Function.identity()).doOnSuccess(unused ->
                logger.debug("deleted player: {}", id)
        ).doOnError(e ->
                logger.error("exception occurred while deleting player: {}, exception: {}", id, e.toString())
        );
    }
}
