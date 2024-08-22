package pl.app.account.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.account.application.domain.Account;
import pl.app.account.application.domain.AccountEvent;
import pl.app.account.application.domain.AccountException;
import pl.app.account.application.port.in.AccountCommand;
import pl.app.account.application.port.in.AccountService;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;



@Service
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<Account> create(AccountCommand.CreateAccountCommand command) {
        logger.debug("creating account: {}", command.getNickname());
        return mongoTemplate.exists(Query.query(Criteria.where("nickname").is(command.getNickname())), Account.class)
                .flatMap(exist -> exist ? Mono.error(AccountException.DuplicatedNameException.fromName(command.getNickname())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating account: {}, exception: {}", command.getNickname(), e.getMessage()))
                .then(Mono.defer(() -> {
                    Account account = new Account(command.getNickname());
                    var event = new AccountEvent.AccountCreatedEvent(
                            account.getId()
                    );
                    return mongoTemplate.insert(account)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getAccountCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created account: {}, with name: {}", saved.getId(), saved.getNickname());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }
}
