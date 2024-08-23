package pl.app.character.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.character.query.dto.CharacterDto;
import reactor.core.publisher.Mono;

public interface CharacterQueryService {
    Mono<CharacterDto> fetchById(@NonNull ObjectId id);

    Mono<Page<CharacterDto>> fetchAllByPageable(Pageable pageable);
}
