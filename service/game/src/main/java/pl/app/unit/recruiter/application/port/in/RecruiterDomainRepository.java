package pl.app.unit.recruiter.application.port.in;

import org.bson.types.ObjectId;
import pl.app.unit.recruiter.application.domain.Recruiter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface RecruiterDomainRepository {
    Mono<Recruiter> fetchByVillageId(ObjectId villageId);

    Flux<Recruiter> fetchByPlayerId(ObjectId playerId);

    Flux<Recruiter> fetchRecruiterWithRequestEnding(Duration withinTime);
}
