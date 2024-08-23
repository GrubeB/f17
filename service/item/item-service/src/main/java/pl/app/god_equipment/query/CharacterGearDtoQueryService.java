package pl.app.god_equipment.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.god_equipment.dto.CharacterGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CharacterGearDtoQueryService {
    Mono<CharacterGearDto> fetchByCharacterId(@NonNull ObjectId characterId);

    Mono<Page<CharacterGearDto>> fetchAllByCharacterIds(List<ObjectId> characterIds, Pageable pageable);
}
