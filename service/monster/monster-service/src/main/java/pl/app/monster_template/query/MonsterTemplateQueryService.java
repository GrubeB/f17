package pl.app.monster_template.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.monster_template.dto.MonsterTemplateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MonsterTemplateQueryService {
    Mono<MonsterTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<MonsterTemplateDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<MonsterTemplateDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
