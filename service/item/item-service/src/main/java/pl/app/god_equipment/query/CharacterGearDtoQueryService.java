package pl.app.god_equipment.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.god_equipment.dto.CharacterGearDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface CharacterGearDtoQueryService {
    Mono<CharacterGearDto> fetchById(@NonNull ObjectId id);
    Mono<Page<CharacterGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
