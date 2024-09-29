package pl.app.character_status.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.character_status.query.dto.CharacterStatusDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CharacterStatusQueryService {
    Mono<CharacterStatusDto> fetchByCharacterId(@NonNull ObjectId characterId);
    Mono<List<CharacterStatusDto>> fetchAllByCharacterIds(List<ObjectId> characterIds);

    Mono<Page<CharacterStatusDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<CharacterStatusDto>> fetchAllByCharacterIds(List<ObjectId> characterIds, Pageable pageable);
}
