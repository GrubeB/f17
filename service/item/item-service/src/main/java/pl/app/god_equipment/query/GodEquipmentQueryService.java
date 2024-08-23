package pl.app.god_equipment.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.god_equipment.dto.GodEquipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GodEquipmentQueryService {
    Mono<GodEquipmentDto> fetchByGodId(@NonNull ObjectId godId);

    Mono<Page<GodEquipmentDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<GodEquipmentDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable);
}
