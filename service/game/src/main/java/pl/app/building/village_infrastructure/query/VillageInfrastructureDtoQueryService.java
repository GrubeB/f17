package pl.app.building.village_infrastructure.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VillageInfrastructureDtoQueryService {
    Mono<VillageInfrastructureDto> fetchByVillageId(@NonNull ObjectId villageId);

    Flux<VillageInfrastructureDto> fetchAll();
}
