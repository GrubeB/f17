package pl.app.god_family.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.god_family.query.dto.GodFamilyWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GodFamilyWithGearDtoQueryService {
    Mono<GodFamilyWithGearDto> fetchByGodId(@NonNull ObjectId godId);
    Mono<Page<GodFamilyWithGearDto>> fetchByPageable(Pageable pageable);
    Mono<Page<GodFamilyWithGearDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable);
}
