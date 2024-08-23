package pl.app.god_family.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.god_family.query.dto.GodFamilyDto;
import pl.app.god_family.query.dto.GodFamilyWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GodFamilyQueryService {
    Mono<GodFamilyDto> fetchByGodId(@NonNull ObjectId id);

    Mono<Page<GodFamilyDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<GodFamilyDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable);
}
