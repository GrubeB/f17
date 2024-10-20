package pl.app.unit.recruiter.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.building.builder.application.domain.Builder;
import pl.app.building.builder.application.domain.BuilderException;
import pl.app.building.builder.application.port.in.BuilderDomainRepository;
import pl.app.unit.recruiter.application.domain.Recruiter;
import pl.app.unit.recruiter.application.domain.RecruiterException;
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
}
