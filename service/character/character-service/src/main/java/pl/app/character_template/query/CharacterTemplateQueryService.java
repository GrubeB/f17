package pl.app.character_template.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.character_template.dto.CharacterTemplateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CharacterTemplateQueryService {
    Mono<CharacterTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<CharacterTemplateDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<CharacterTemplateDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
