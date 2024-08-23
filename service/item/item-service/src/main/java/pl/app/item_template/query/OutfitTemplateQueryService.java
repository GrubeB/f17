package pl.app.item_template.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import reactor.core.publisher.Mono;

public interface OutfitTemplateQueryService {
    Mono<OutfitTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<OutfitTemplateDto>> fetchAllByPageable(Pageable pageable);
}
