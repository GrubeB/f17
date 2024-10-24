package pl.app.player.player.application.port.in;

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
import pl.app.item.inventory.application.port.in.InventoryCommand;
import pl.app.item.inventory.application.port.in.InventoryService;
import pl.app.money.player_money.application.port.in.PlayerMoneyCommand;
import pl.app.money.player_money.application.port.in.PlayerMoneyService;
import pl.app.player.player.application.domain.Player;
import pl.app.player.player.application.domain.PlayerEvent;
import pl.app.player.player.application.domain.PlayerException;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class PlayerServiceImpl implements PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final PlayerMoneyService playerMoneyService;
    private final InventoryService inventoryService;

    @Override
    public Mono<Player> crate(PlayerCommand.CreatePlayerCommand command) {
        logger.debug("crating player: {}", command.getName());
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), Player.class)
                .flatMap(exist -> exist ? Mono.error(PlayerException.DuplicatedPlayerNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating player: {}, exception: {}", command.getName(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new Player(command.getAccountId());
                    var event = new PlayerEvent.PlayerCreatedEvent(domain.getPlayerId());
                    return playerMoneyService.crate(new PlayerMoneyCommand.CreatePlayerMoneyCommand(domain.getPlayerId()))
                            .then(inventoryService.create(new InventoryCommand.CreateInventoryCommand(domain.getPlayerId())))
                            .then(mongoTemplate.insert(domain))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getPlayerCreated().getName(), saved.getPlayerId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("created player: {}", saved.getPlayerId()));
                }));
    }


}
