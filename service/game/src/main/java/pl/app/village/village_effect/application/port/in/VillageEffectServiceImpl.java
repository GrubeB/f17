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
import reactor.core.publisher.Mono;

import java.time.Instant;


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
        logger.debug("crating village effect: {}", command.getVillageId());
        return mongoTemplate.exists(Query.query(Criteria.where("playerId").is(command.getVillageId().toHexString())), VillageEffect.class)
                .flatMap(exist -> exist ? Mono.error(VillageEffectException.DuplicatedVillageIdException.fromId(command.getVillageId().toHexString())) : Mono.empty())
                .doOnError(e -> logger.error("exception occurred while crating village effect: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .then(Mono.defer(() -> {
                    var domain = new VillageEffect(command.getVillageId());
                    var event = new VillageEffectEvent.VillageEffectCreatedEvent(domain.getVillageId());
                    return mongoTemplate.insert(domain)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getVillageEffectCreated().getName(), saved.getVillageId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> logger.debug("created village effect: {}", saved.getVillageId()));
                }));
    }

    @Override
    public Mono<VillageEffect> add(VillageEffectCommand.AddEffectCommand command) {
        logger.debug("adding effect to village: {}", command.getVillageId());
        return villageEffectDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while adding effect to village: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.add(new VillageEffect.Effect(command.getEffectType(), command.getValue(), Instant.now(), Instant.now().plus(command.getDuration())));
                    return mongoTemplate.save(domain)
                            .doOnSuccess(saved -> logger.debug("added effect to village: {}", saved.getVillageId()));
                });
    }

    @Override
    public Mono<VillageEffect> remove(VillageEffectCommand.RemoveInvalidEffectsCommand command) {
        logger.debug("removing effects from village: {}", command.getVillageId());
        return villageEffectDomainRepository.fetchByVillageId(command.getVillageId())
                .doOnError(e -> logger.error("exception occurred while removing effects from village: {}, exception: {}", command.getVillageId(), e.getMessage()))
                .flatMap(domain -> {
                    domain.removeInvalidEffect();
                    return mongoTemplate.save(domain)
                            .doOnSuccess(saved -> logger.debug("removed effects from village: {}", saved.getVillageId()));
                });
    }
}
