package pl.app.character.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.character.query.dto.CharacterDto;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.god_equipment.dto.GodEquipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface CharacterWithGearDtoQueryService {
    Mono<CharacterWithGearDto> fetchById(@NonNull ObjectId id);
    Mono<Page<CharacterWithGearDto>> fetchByPageable(Pageable pageable);
    Mono<Page<CharacterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
