package pl.app.building.builder.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.domain.Builder;
import pl.app.building.builder.application.domain.BuilderException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
class BuilderDomainRepositoryImpl implements BuilderDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Builder> fetchByVillageId(ObjectId villageId) {
        return mongoTemplate.query(Builder.class)
                .matching(Query.query(Criteria.where("_id").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> BuilderException.NotFoundBuilderException.fromId(villageId.toHexString())));
    }

    @Override
    public Flux<Builder> fetchBuildersWithConstructEnding(Duration withinTime) {
        Instant currentTime = Instant.now();
        Instant toTimeThreshold = currentTime.plus(withinTime);

        return mongoTemplate.query(Builder.class)
                .matching(Query.query(Criteria.where("constructs.to").lte(toTimeThreshold)))
                .all();
    }

    @Override
    public Flux<Builder> fetchBuildersWithConstructionStarting(Duration withinTime) {
        Instant currentTime = Instant.now();
        Instant toTimeThreshold = currentTime.plus(withinTime);

        return mongoTemplate.query(Builder.class)
                .matching(Query.query(Criteria.where("constructs.from").lte(toTimeThreshold).and("constructs.started").is(false)))
                .all();
    }
}
