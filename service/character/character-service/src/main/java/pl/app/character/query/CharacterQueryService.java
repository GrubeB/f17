package pl.app.character.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.character.query.dto.CharacterDto;
import reactor.core.publisher.Mono;

public interface CharacterQueryService {
    Mono<CharacterDto> fetchById(@NonNull ObjectId id);

    Mono<Page<CharacterDto>> fetchByPageable(Pageable pageable);
}
