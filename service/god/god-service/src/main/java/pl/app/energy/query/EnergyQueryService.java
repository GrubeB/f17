package pl.app.energy.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.energy.query.dto.EnergyDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EnergyQueryService {
    Mono<EnergyDto> fetchByGodId(@NonNull ObjectId godId);

    Mono<List<EnergyDto>> fetchAllByGodIds(List<ObjectId> godIds);

    Mono<Page<EnergyDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<EnergyDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable);
}
