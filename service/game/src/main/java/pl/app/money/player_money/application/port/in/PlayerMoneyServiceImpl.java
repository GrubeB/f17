package pl.app.money.player_money.application.port.in;

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
import pl.app.money.money.application.domain.Money;
import pl.app.money.player_money.application.domain.PlayerMoney;
import pl.app.money.player_money.application.domain.PlayerMoneyEvent;
import pl.app.money.player_money.application.domain.PlayerMoneyException;
import pl.app.player.player.application.domain.PlayerEvent;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.application.domain.VillageResourceEvent;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class PlayerMoneyServiceImpl implements PlayerMoneyService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerMoneyServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final PlayerMoneyDomainRepository playerMoneyDomainRepository;

    @Override
    public Mono<PlayerMoney> crate(PlayerMoneyCommand.CreatePlayerMoneyCommand command) {
        logger.debug("crating player money: {}", command.getPlayerId());
        return mongoTemplate.exists(Query.query(Criteria.where("playerId").is(command.getPlayerId().toHexString())), PlayerMoney.class)
                .flatMap(exist -> exist ? Mono.error(PlayerMoneyException.DuplicatedPlayerIdException.fromId(command.getPlayerId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating player money: {}, exception: {}", command.getPlayerId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new PlayerMoney(command.getPlayerId(), new Money(100));
                    var event = new PlayerMoneyEvent.PlayerMoneyCreatedEvent(domain.getPlayerId(), domain.getMoney());
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getPlayerMoneyCreated().getName(), saved.getPlayerId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("created player money: {}", saved.getPlayerId()));
                }));
    }

    @Override
    public Mono<PlayerMoney> add(PlayerMoneyCommand.AddMoneyCommand command) {
        logger.debug("adding money to player: {}", command.getPlayerId());
        return playerMoneyDomainRepository.fetchByPlayerId(command.getPlayerId())
                .doOnError(e -> logger.error("exception occurred while adding money to player: {}, exception: {}", command.getPlayerId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.add(command.getMoney());
                    var event = new PlayerMoneyEvent.MoneyAddedEvent(domain.getPlayerId(), command.getMoney());
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneyAdded().getName(), saved.getPlayerId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("added money to player: {}", saved.getPlayerId()));
                });
    }

    @Override
    public Mono<PlayerMoney> subtract(PlayerMoneyCommand.SubtractMoneyCommand command) {
        logger.debug("subtracting money to player: {}", command.getPlayerId());
        return playerMoneyDomainRepository.fetchByPlayerId(command.getPlayerId())
                .doOnError(e -> logger.error("exception occurred while subtracting money to player: {}, exception: {}", command.getPlayerId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.subtract(command.getMoney());
                    var event = new PlayerMoneyEvent.MoneySubtractedEvent(domain.getPlayerId(), command.getMoney());
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneySubtracted().getName(), saved.getPlayerId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("subtracted money to player: {}", saved.getPlayerId()));
                });
    }
}
