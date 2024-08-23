package pl.app.item.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Mono;

public interface WeaponQueryService {
    Mono<WeaponDto> fetchById(@NonNull ObjectId id);

    Mono<Page<WeaponDto>> fetchAllByPageable(Pageable pageable);
}
