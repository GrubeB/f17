package pl.app.god.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

public interface GodQueryService {
    Mono<GodDto> fetchById(@NonNull ObjectId id);

    Mono<Page<GodDto>> fetchByPageable(Pageable pageable);
}
