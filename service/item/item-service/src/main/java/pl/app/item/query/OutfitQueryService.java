package pl.app.item.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.item.query.dto.OutfitDto;
import reactor.core.publisher.Mono;

public interface OutfitQueryService {
    Mono<OutfitDto> fetchById(@NonNull ObjectId id);

    Mono<Page<OutfitDto>> fetchAllByPageable(Pageable pageable);
}
