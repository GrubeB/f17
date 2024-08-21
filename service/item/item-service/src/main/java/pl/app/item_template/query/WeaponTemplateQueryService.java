package pl.app.item_template.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Mono;

public interface WeaponTemplateQueryService {
    Mono<WeaponTemplateDto> fetchById(@NonNull ObjectId id);

    Mono<Page<WeaponTemplateDto>> fetchByPageable(Pageable pageable);
}
