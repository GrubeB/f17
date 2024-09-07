package pl.app.monster.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.monster.query.dto.MonsterDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MonsterQueryService {
    Mono<MonsterDto> fetchById(@NonNull ObjectId id);

    Mono<Page<MonsterDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<MonsterDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
