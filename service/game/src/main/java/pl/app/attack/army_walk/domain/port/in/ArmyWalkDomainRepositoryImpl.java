package pl.app.attack.army_walk.domain.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.application.ArmyWalkException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
class ArmyWalkDomainRepositoryImpl implements ArmyWalkDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<ArmyWalk> fetchById(ObjectId attackId) {
        return mongoTemplate.query(ArmyWalk.class)
                .matching(Query.query(Criteria.where("_id").is(attackId)))
                .one()
                .switchIfEmpty(Mono.error(() -> ArmyWalkException.NotFoundArmyWalkException.fromId(attackId.toHexString())));
    }

    @Override
    public Flux<ArmyWalk> fetchArmyWalkWithEnding(Duration withinTime) {
        Instant currentTime = Instant.now();
        Instant toTimeThreshold = currentTime.plus(withinTime);

        return mongoTemplate.query(ArmyWalk.class)
                .matching(Query.query(Criteria.where("arriveDate").lte(toTimeThreshold)))
                .all();
    }
}
