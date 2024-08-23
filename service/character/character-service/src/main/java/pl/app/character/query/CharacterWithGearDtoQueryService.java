package pl.app.character.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.character.query.dto.CharacterWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CharacterWithGearDtoQueryService {
    Mono<CharacterWithGearDto> fetchById(@NonNull ObjectId id);
    Mono<Page<CharacterWithGearDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<CharacterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
