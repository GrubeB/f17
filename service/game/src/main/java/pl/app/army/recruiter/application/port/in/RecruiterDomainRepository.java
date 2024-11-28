package pl.app.army.recruiter.application.port.in;

import org.bson.types.ObjectId;
import pl.app.army.recruiter.application.domain.Recruiter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface RecruiterDomainRepository {
    Mono<Recruiter> fetchByVillageId(ObjectId villageId);

    Flux<Recruiter> fetchByPlayerId(ObjectId playerId);

    Flux<Recruiter> fetchRecruiterWithRequestEnding(Duration withinTime);

    Flux<Recruiter> fetchRecruiterWithRequestStarting(Duration withinTime);
}
