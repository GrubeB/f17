package pl.app.god.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

public interface GodQueryService {
    Mono<GodDto> fetchById(@NonNull ObjectId id);

    Mono<Page<GodDto>> fetchAllByPageable(Pageable pageable);
}
