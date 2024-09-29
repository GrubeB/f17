package pl.app.god.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.god.query.dto.GodAggregateDto;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GodAggregateQueryService {
    Mono<GodAggregateDto> fetchById(@NonNull ObjectId id);
    Mono<Page<GodAggregateDto>> fetchAllByPageable(Pageable pageable);
}
