package pl.app.item.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.item.query.dto.WeaponDto;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Mono;

public interface WeaponQueryService {
    Mono<WeaponDto> fetchById(@NonNull ObjectId id);

    Mono<Page<WeaponDto>> fetchByPageable(Pageable pageable);
}
