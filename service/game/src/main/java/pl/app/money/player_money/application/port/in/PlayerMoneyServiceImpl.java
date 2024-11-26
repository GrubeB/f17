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
import pl.app.money.share.Money;
import pl.app.money.player_money.application.domain.PlayerMoney;
import pl.app.money.player_money.application.domain.PlayerMoneyEvent;
import pl.app.money.player_money.application.domain.PlayerMoneyException;
import reactor.core.publisher.Mono;

import java.util.function.Function;


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
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("playerId").is(command.getPlayerId().toHexString())), PlayerMoney.class)
                        .flatMap(exist -> exist ? Mono.error(PlayerMoneyException.DuplicatedPlayerIdException.fromId(command.getPlayerId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new PlayerMoney(command.getPlayerId(), new Money(100));
                            var event = new PlayerMoneyEvent.PlayerMoneyCreatedEvent(domain.getPlayerId(), domain.getMoney());
                            return mongoTemplate.insert(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getPlayerMoneyCreated().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating player money: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created player money: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while crating player money: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }

    @Override
    public Mono<PlayerMoney> add(PlayerMoneyCommand.AddMoneyCommand command) {
        return Mono.fromCallable(() ->
                playerMoneyDomainRepository.fetchByPlayerId(command.getPlayerId())
                        .flatMap(domain -> {
                            domain.add(command.getMoney());
                            var event = new PlayerMoneyEvent.MoneyAddedEvent(domain.getPlayerId(), command.getMoney());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneyAdded().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("adding money to player: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("added money to player: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while adding money to player: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }

    @Override
    public Mono<PlayerMoney> subtract(PlayerMoneyCommand.SubtractMoneyCommand command) {
        return Mono.fromCallable(() ->
                playerMoneyDomainRepository.fetchByPlayerId(command.getPlayerId())
                        .flatMap(domain -> {
                            domain.subtract(command.getMoney());
                            var event = new PlayerMoneyEvent.MoneySubtractedEvent(domain.getPlayerId(), command.getMoney());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneySubtracted().getName(), domain.getPlayerId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("subtracting money from player: {}", command.getPlayerId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("subtracted money from player: {}", domain.getPlayerId())
        ).doOnError(e ->
                logger.error("exception occurred while subtracting money from player: {}, exception: {}", command.getPlayerId(), e.toString())
        );
    }
}
