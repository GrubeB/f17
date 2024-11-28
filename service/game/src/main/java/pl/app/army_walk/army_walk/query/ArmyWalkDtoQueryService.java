package pl.app.army_walk.army_walk.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.army_walk.army_walk.query.dto.ArmyWalkDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArmyWalkDtoQueryService {
    Mono<ArmyWalkDto> fetchById(@NonNull ObjectId id);

    Flux<ArmyWalkDto> fetchAll();
}
