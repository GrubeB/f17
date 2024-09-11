package pl.app.family.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.family.query.dto.FamilyWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FamilyWithGearDtoQueryService {
    Mono<FamilyWithGearDto> fetchByGodId(@NonNull ObjectId godId);
    Mono<Page<FamilyWithGearDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<FamilyWithGearDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable);
}
