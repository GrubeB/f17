package pl.app.unit.recruiter.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.unit.recruiter.query.dto.RecruiterDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecruiterDtoQueryService {
    Mono<RecruiterDto> fetchByVillageId(@NonNull ObjectId villageId);

    Flux<RecruiterDto> fetchAll();
}
