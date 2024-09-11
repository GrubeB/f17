package pl.app.family.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.family.query.dto.FamilyDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FamilyQueryService {
    Mono<FamilyDto> fetchByGodId(@NonNull ObjectId id);

    Mono<Page<FamilyDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<FamilyDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable);
}
