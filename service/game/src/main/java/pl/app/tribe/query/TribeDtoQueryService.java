package pl.app.tribe.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.tribe.query.dto.TribeDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TribeDtoQueryService {
    Mono<TribeDto> fetchByVillageId(@NonNull ObjectId villageId);

    Flux<TribeDto> fetchAll();
}
