package pl.app.god_template.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.god_template.application.domain.GodTemplate;
import pl.app.god_template.query.dto.GodTemplateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GodTemplateQueryService {
    Mono<GodTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<GodTemplateDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<GodTemplateDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
