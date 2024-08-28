package pl.app.god.application;

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
import pl.app.god.application.domain.God;
import pl.app.god.application.domain.GodEvent;
import pl.app.god.application.domain.GodException;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodService;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class GodServiceImpl implements GodService {
    private static final Logger logger = LoggerFactory.getLogger(GodServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<God> create(GodCommand.CreateGodCommand command) {
        logger.debug("creating god with name: {}, for account: {}", command.getName(), command.getAccountId());
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(command.getName())), God.class)
                .flatMap(exist -> exist ? Mono.error(GodException.DuplicatedNameException.fromName(command.getName())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating god with name : {}, for account: {}, exception: {}", command.getName(), command.getAccountId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    God god = new God(command.getAccountId(), command.getName());
                    god.getMoney().addMoney(Money.Type.BASE, 10_000L);
                    var event = new GodEvent.GodCreatedEvent(
                            god.getId()
                    );
                    return mongoTemplate.insert(god)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getGodCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created god: {}, with name: {}, for account: {}", saved.getId(), saved.getName(), saved.getAccountId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }
}
