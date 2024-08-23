package pl.app.item_template.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Mono;

public interface WeaponTemplateQueryService {
    Mono<WeaponTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<WeaponTemplateDto>> fetchAllByPageable(Pageable pageable);
}
