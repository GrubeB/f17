package pl.app.village.village_effect.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.village.village_effect.application.domain.VillageEffect;
import pl.app.village.village_effect.application.domain.VillageEffectException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
class VillageEffectDomainRepositoryImpl implements VillageEffectDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<VillageEffect> fetchByVillageId(ObjectId villageId) {
        return mongoTemplate.query(VillageEffect.class)
                .matching(Query.query(Criteria.where("_id").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> VillageEffectException.NotFoundVillageEffectException.fromId(villageId.toHexString())));
    }

    @Override
    public Flux<VillageEffect> fetchVillageEffectWithEnding(Duration withinTime) {
        Instant currentTime = Instant.now();
        Instant toTimeThreshold = currentTime.plus(withinTime);

        return mongoTemplate.query(VillageEffect.class)
                .matching(Query.query(Criteria.where("effects.to").lte(toTimeThreshold)))
                .all();
    }
}
