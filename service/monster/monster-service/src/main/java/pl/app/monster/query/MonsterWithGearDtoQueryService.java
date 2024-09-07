package pl.app.monster.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.monster.query.dto.MonsterWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MonsterWithGearDtoQueryService {
    Mono<MonsterWithGearDto> fetchById(@NonNull ObjectId id);
    Mono<Page<MonsterWithGearDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<MonsterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
