package pl.app.equipment.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.equipment.dto.EquipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EquipmentQueryService {
    Mono<EquipmentDto> fetchByGodId(@NonNull ObjectId godId);

    Mono<Page<EquipmentDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<EquipmentDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable);
}
