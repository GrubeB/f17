package pl.app.village.loyalty.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.village.loyalty.application.domain.VillageLoyalty;
import pl.app.village.loyalty.application.domain.VillageLoyaltyEvent;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
class VillageLoyaltyServiceImpl implements VillageLoyaltyService {
    private static final Logger logger = LoggerFactory.getLogger(VillageLoyaltyServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageLoyaltyDomainRepository domainRepository;

    @Override
    public Mono<VillageLoyalty> create(VillageLoyaltyCommand.CreateVillageLoyaltyCommand command) {
        return Mono.fromCallable(() -> {
            var domain = new VillageLoyalty(command.getVillageId());
            var event = new VillageLoyaltyEvent.VillageLoyaltyCreatedEvent(domain.getVillageId());
            return mongoTemplate.insert(domain)
                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageLoyaltyCreated().getName(), domain.getVillageId(), event)))
                    .thenReturn(domain);
        }).doOnSubscribe(subscription ->
                logger.debug("crating village loyalty: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created village loyalty: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while crating village loyalty: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageLoyalty> subtract(VillageLoyaltyCommand.SubtractLoyaltyCommand command) {
        return Mono.fromCallable(() ->
                domainRepository.fetchById(command.getVillageId())
                        .flatMap(domain -> {
                            domain.subtractLoyalty(command.getAmount());
                            var event = new VillageLoyaltyEvent.LoyaltySubtractedEvent(domain.getVillageId(), command.getAmount());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getLoyaltySubtracted().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("subtracting loyalty in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("subtracted loyalty in village: {}", command.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while subtracting loyalty in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageLoyalty> refresh(VillageLoyaltyCommand.RefreshLoyaltyCommand command) {
        return Mono.fromCallable(() ->
                domainRepository.fetchById(command.getVillageId())
                        .flatMap(domain -> {
                            var loyalty = domain.refreshLoyalty();
                            var event = new VillageLoyaltyEvent.LoyaltyAddedEvent(domain.getVillageId(), loyalty);
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getLoyaltyAdded().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("refreshing loyalty in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("refreshed loyalty in village: {}", command.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while refreshing loyalty in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageLoyalty> reset(VillageLoyaltyCommand.ResetLoyaltyCommand command) {
        return Mono.fromCallable(() ->
                domainRepository.fetchById(command.getVillageId())
                        .flatMap(domain -> {
                            domain.reset();
                            var event = new VillageLoyaltyEvent.LoyaltyResetEvent(domain.getVillageId(), domain.getLoyalty());
                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getLoyaltyReset().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("reset loyalty in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("reset loyalty in village: {}", command.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while reset loyalty in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

}
