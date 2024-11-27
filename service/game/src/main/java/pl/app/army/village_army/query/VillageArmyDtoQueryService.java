package pl.app.army.village_army.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.army.village_army.query.dto.VillageArmyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VillageArmyDtoQueryService {
    Mono<VillageArmyDto> fetchByVillageId(@NonNull ObjectId villageId);

    Flux<VillageArmyDto> fetchAll();
}
