package pl.app.village.village_effect.application.port.in;

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
import pl.app.village.village_effect.application.domain.VillageEffect;
import pl.app.village.village_effect.application.domain.VillageEffectEvent;
import pl.app.village.village_effect.application.domain.VillageEffectException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
class VillageEffectServiceImpl implements VillageEffectService {
    private static final Logger logger = LoggerFactory.getLogger(VillageEffectServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    private final VillageEffectDomainRepository villageEffectDomainRepository;

    @Override
    public Mono<VillageEffect> crate(VillageEffectCommand.CreateVillageEffectCommand command) {
        return Mono.fromCallable(() ->
                mongoTemplate.exists(Query.query(Criteria.where("villageId").is(command.getVillageId().toHexString())), VillageEffect.class)
                        .flatMap(exist -> exist ? Mono.error(VillageEffectException.DuplicatedVillageIdException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                        .then(Mono.defer(() -> {
                            var domain = new VillageEffect(command.getVillageId());
                            var event = new VillageEffectEvent.VillageEffectCreatedEvent(domain.getVillageId());
                            return mongoTemplate.insert(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageEffectCreated().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        }))
        ).doOnSubscribe(subscription ->
                logger.debug("crating village effect for village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created village effect for village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while crating village effect for village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageEffect> start(VillageEffectCommand.StartEffectCommand command) {
        return Mono.fromCallable(() ->
                villageEffectDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            domain.start(new VillageEffect.Effect(command.getEffectType(), command.getValue(), Instant.now(), Instant.now().plus(command.getDuration())));
                            var event = new VillageEffectEvent.VillageEffectStartedEvent(domain.getVillageId());

                            return mongoTemplate.save(domain)
                                    .then(Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageEffectStarted().getName(), domain.getVillageId(), event)))
                                    .thenReturn(domain);
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("starting effect in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("started effect in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while starting effect in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageEffect> remove(VillageEffectCommand.RemoveInvalidEffectsCommand command) {
        return Mono.fromCallable(() ->
                villageEffectDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            Set<VillageEffect.Effect> effects = domain.removeInvalidEffects();
                            if (effects.isEmpty()) {
                                return Mono.just(domain);
                            }
                            return Flux.fromIterable(effects)
                                    .map(e -> new VillageEffectEvent.VillageEffectExpiredEvent(domain.getVillageId()))
                                    .flatMap(e -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageEffectExpired().getName(), domain.getVillageId(), e)))
                                    .then(mongoTemplate.save(domain));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("removing effect from village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed effect from village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while removing effect from village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }

    @Override
    public Mono<VillageEffect> reject(VillageEffectCommand.RejectAllEffectsCommand command) {
        return Mono.fromCallable(() ->
                villageEffectDomainRepository.fetchByVillageId(command.getVillageId())
                        .flatMap(domain -> {
                            Set<VillageEffect.Effect> effects = domain.rejectAllEffects();
                            if (effects.isEmpty()) {
                                return Mono.just(domain);
                            }
                            return Flux.fromIterable(effects)
                                    .map(e -> new VillageEffectEvent.VillageEffectRejectedEvent(domain.getVillageId()))
                                    .flatMap(e -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageEffectRejected().getName(), domain.getVillageId(), e)))
                                    .then(mongoTemplate.save(domain));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("rejecting effect in village: {}", command.getVillageId())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("rejected effect in village: {}", domain.getVillageId())
        ).doOnError(e ->
                logger.error("exception occurred while rejecting effect in village: {}, exception: {}", command.getVillageId(), e.toString())
        );
    }
}
