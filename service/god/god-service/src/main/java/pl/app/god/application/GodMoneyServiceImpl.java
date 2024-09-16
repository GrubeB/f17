package pl.app.god.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.common.shared.model.Money;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.god.application.domain.God;
import pl.app.god.application.domain.GodEvent;
import pl.app.god.application.domain.GodMoney;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodMoneyService;
import pl.app.god.application.port.out.GodDomainRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


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
        logger.debug("adding money to god: {}", command.getGodId());
        return domainRepository.fetchById(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while adding money to god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    Map<Money.Type, Long> monies = command.getMoney().getMonies();
                    if (monies.isEmpty()) {
                        return Mono.empty();
                    }
                    Set<GodEvent.MoneyAddedEvent> events = monies.entrySet().stream().map((e) -> {
                                if (e.getValue() == 0) {
                                    return null;
                                }
                                domain.getMoney().addMoney(e.getKey(), e.getValue());
                                return new GodEvent.MoneyAddedEvent(
                                        domain.getId(),
                                        e.getKey(),
                                        e.getValue()
                                );
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Flux.fromIterable(events)
                                    .flatMap(event -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneyAdded().getName(), saved.getId(), event))
                                            .doOnSuccess(unused -> {
                                                logger.debug("added money to god: {}", command.getGodId());
                                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                                            }).then())
                                    .then(Mono.just(saved)));
                });
    }

    @Override
    public Mono<God> subtractMoney(GodCommand.SubtractMoneyCommand command) {
        logger.debug("subtracting money from god: {}", command.getGodId());
        return domainRepository.fetchById(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while subtracting money from god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    Map<Money.Type, Long> monies = command.getMoney().getMonies();
                    if (monies.isEmpty()) {
                        return Mono.empty();
                    }
                    Set<GodEvent.MoneySubtractedEvent> events = monies.entrySet().stream().map((e) -> {
                                if (e.getValue() == 0) {
                                    return null;
                                }
                                domain.setMoney(new GodMoney(domain.getMoney().subtractMoney(e.getKey(), e.getValue())));
                                return new GodEvent.MoneySubtractedEvent(
                                        domain.getId(),
                                        e.getKey(),
                                        e.getValue()
                                );
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Flux.fromIterable(events)
                                    .flatMap(event -> Mono.fromFuture(kafkaTemplate.send(topicNames.getMoneySubtracted().getName(), saved.getId(), event))
                                            .doOnSuccess(unused -> {
                                                logger.debug("subtracted money to god: {}", command.getGodId());
                                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                                            }).then())
                                    .then(Mono.just(saved)));
                });
    }
}
