package pl.app.item.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import reactor.core.publisher.Mono;

public interface OutfitQueryService {
    Mono<OutfitDto> fetchById(@NonNull ObjectId id);

    Mono<Page<OutfitDto>> fetchByPageable(Pageable pageable);
}
