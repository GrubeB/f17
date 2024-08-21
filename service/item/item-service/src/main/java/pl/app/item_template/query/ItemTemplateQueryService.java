package pl.app.item_template.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.item_template.query.dto.ItemTemplateDto;
import reactor.core.publisher.Mono;

public interface ItemTemplateQueryService {
    Mono<ItemTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<ItemTemplateDto>> fetchByPageable(Pageable pageable);
}
