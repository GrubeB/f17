package pl.app.god_equipment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.god_equipment.dto.GodEquipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GodEquipmentQueryService {
    Mono<GodEquipmentDto> fetchById(@NonNull ObjectId godId);
    Mono<Page<GodEquipmentDto>> fetchByPageable(Pageable pageable);
    Mono<Page<GodEquipmentDto>> fetchAllByIds(List<ObjectId> godIds, Pageable pageable);
}
