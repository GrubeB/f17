package pl.app.building.builder.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.building.builder.query.dto.BuilderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BuilderDtoQueryService {
    Mono<BuilderDto> fetchByVillageId(@NonNull ObjectId villageId);

    Flux<BuilderDto> fetchAll();
}
