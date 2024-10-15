package pl.app.village.village.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.village.village.query.dto.VillageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VillageDtoQueryService {
    Mono<VillageDto> fetchById(@NonNull ObjectId id);

    Flux<VillageDto> fetchAll();
}
