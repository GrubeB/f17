package pl.app.army.recruiter.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.army.recruiter.application.domain.Recruiter;
import pl.app.army.recruiter.application.domain.RecruiterException;
import pl.app.village.village.application.domain.Village;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
class RecruiterDomainRepositoryImpl implements RecruiterDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Recruiter> fetchByVillageId(ObjectId villageId) {
        return mongoTemplate.query(Recruiter.class)
                .matching(Query.query(Criteria.where("_id").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> RecruiterException.NotFoundRecruiterException.fromId(villageId.toHexString())));
    }

    @Override
    public Flux<Recruiter> fetchRecruiterWithRequestEnding(Duration withinTime) {
        Instant currentTime = Instant.now();
        Instant toTimeThreshold = currentTime.plus(withinTime);

        return mongoTemplate.query(Recruiter.class)
                .matching(Query.query(Criteria.where("requests.to").lte(toTimeThreshold)))
                .all();
    }

    @Override
    public Flux<Recruiter> fetchByPlayerId(ObjectId playerId) {
        return mongoTemplate.query(Village.class)
                .matching(Query.query(Criteria.where("ownerId").is(playerId)))
                .all()
                .flatMap(village -> mongoTemplate.query(Recruiter.class)
                        .matching(Query.query(Criteria.where("_id").is(village.getId()))).one()
                );
    }

    @Override
    public Flux<Recruiter> fetchRecruiterWithRequestStarting(Duration withinTime) {
        Instant currentTime = Instant.now();
        Instant toTimeThreshold = currentTime.plus(withinTime);

        return mongoTemplate.query(Recruiter.class)
                .matching(Query.query(Criteria.where("requests.from").lte(toTimeThreshold).and("requests.started").is(false)))
                .all();
    }
}
