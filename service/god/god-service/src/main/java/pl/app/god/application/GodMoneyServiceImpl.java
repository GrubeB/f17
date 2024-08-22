package pl.app.god.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.god.application.domain.God;
import pl.app.god.application.domain.GodEvent;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodMoneyService;
import pl.app.god.application.port.out.GodDomainRepository;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class GodMoneyServiceImpl implements GodMoneyService {
    private static final Logger logger = LoggerFactory.getLogger(GodMoneyServiceImpl.class);

    private final GodDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<God> addMoney(GodCommand.AddMoneyCommand command) {
        logger.debug("adding {} money to god: {}", command.getAmount(), command.getGodId());
        return domainRepository.fetchById(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while adding {} money to god: {}, exception: {}", command.getAmount(), command.getGodId(), e.getMessage()))
                .flatMap(god -> {
                    god.getMoney().addMoney(command.getAmount());
                    var event = new GodEvent.MoneyAddedEvent(
                            god.getId(),
                            command.getAmount()
                    );
                    return mongoTemplate.save(god)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneyAdded().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added {} money to god: {}", command.getAmount(), command.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<God> subtractMoney(GodCommand.SubtractMoneyCommand command) {
        logger.debug("subtracting {} money from god: {}", command.getAmount(), command.getGodId());
        return domainRepository.fetchById(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while subtracting {} money from god: {}, exception: {}", command.getAmount(), command.getGodId(), e.getMessage()))
                .flatMap(god -> {
                    god.getMoney().subtractMoney(command.getAmount());
                    var event = new GodEvent.MoneySubtractedEvent(
                            god.getId(),
                            command.getAmount()
                    );
                    return mongoTemplate.save(god)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneySubtracted().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("subtracted {} money from god: {}", command.getAmount(), command.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
