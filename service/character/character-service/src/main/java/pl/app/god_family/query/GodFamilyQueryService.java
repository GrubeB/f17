package pl.app.god_family.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

public interface GodFamilyQueryService {
    Mono<GodFamilyDto> fetchById(@NonNull ObjectId id);

    Mono<Page<GodFamilyDto>> fetchByPageable(Pageable pageable);
}
