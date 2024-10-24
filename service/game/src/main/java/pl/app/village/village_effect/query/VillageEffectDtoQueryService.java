package pl.app.village.village_effect.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.village.village_effect.query.dto.VillageEffectDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VillageEffectDtoQueryService {
    Mono<VillageEffectDto> fetchByVillageId(@NonNull ObjectId villageId);

    Flux<VillageEffectDto> fetchAll();
}
