package pl.app.item_template.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import reactor.core.publisher.Mono;

public interface OutfitTemplateQueryService {
    Mono<OutfitTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<OutfitTemplateDto>> fetchByPageable(Pageable pageable);
}
