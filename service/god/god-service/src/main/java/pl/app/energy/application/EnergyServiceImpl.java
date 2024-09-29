package pl.app.energy.application;

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
import pl.app.energy.application.domain.Energy;
import pl.app.energy.application.domain.EnergyEvent;
import pl.app.energy.application.domain.EnergyException;
import pl.app.energy.application.port.in.EnergyCommand;
import pl.app.energy.application.port.in.EnergyDomainRepository;
import pl.app.energy.application.port.in.EnergyService;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
class EnergyServiceImpl implements EnergyService {
    private static final Logger logger = LoggerFactory.getLogger(EnergyServiceImpl.class);

    private final EnergyDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<Energy> create(EnergyCommand.CreateEnergyCommand command) {
        logger.debug("creating energy for god with id: {}", command.getGodId());
        return mongoTemplate.exists(Query.query(Criteria.where("godId").is(command.getGodId())), Energy.class)
                .flatMap(exist -> exist ? Mono.error(EnergyException.DuplicatedGodException.fromGodId(command.getGodId())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while creating energy for god with id: {}, exception: {}", command.getGodId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new Energy(command.getGodId());
                    var event = new EnergyEvent.EnergyCreatedEvent(
                            domain.getId(),
                            domain.getGodId()
                    );
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getEnergyCreated().getName(), saved.getGodId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created energy for god: {}", saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    @Override
    public Mono<Energy> refreshEnergy(EnergyCommand.RefreshEnergyCommand command) {
        logger.debug("refreshing energy for god: {}", command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while refreshing energy for god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    long amount = domain.regenerateEnergy();
                    var event = new EnergyEvent.EnergyAddedEvent(
                            domain.getGodId(),
                            amount
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getEnergyAdded().getName(), saved.getGodId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("refreshed energy to god: {}", saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Energy> addEnergy(EnergyCommand.AddEnergyCommand command) {
        logger.debug("adding energy to god: {}", command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while adding energy to god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.addEnergy(command.getAmount());
                    var event = new EnergyEvent.EnergyAddedEvent(
                            domain.getGodId(),
                            command.getAmount()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getEnergyAdded().getName(), saved.getGodId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("added energy to god: {}", saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }

    @Override
    public Mono<Energy> subtractEnergy(EnergyCommand.SubtractEnergyCommand command) {
        logger.debug("subtracting energy from god: {}", command.getGodId());
        return domainRepository.fetchByGodId(command.getGodId())
                .doOnError(e -> logger.error("exception occurred while subtracting energy from god: {}, exception: {}", command.getGodId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.subtractEnergy(command.getAmount());
                    var event = new EnergyEvent.EnergySubtractedEvent(
                            domain.getGodId(),
                            command.getAmount()
                    );
                    return mongoTemplate.save(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getEnergySubtracted().getName(), saved.getGodId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("subtracted energy to god: {}", saved.getGodId());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                });
    }
}
