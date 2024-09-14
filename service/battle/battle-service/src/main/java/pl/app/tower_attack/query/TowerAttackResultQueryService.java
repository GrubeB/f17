package pl.app.tower_attack.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.tower_attack.query.dto.TowerAttackResultDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TowerAttackResultQueryService {
    Mono<TowerAttackResultDto> fetchById(@NonNull ObjectId id);

    Mono<Page<TowerAttackResultDto>> fetchAllByPageable(Pageable pageable);

    Mono<Page<TowerAttackResultDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);

    Mono<Page<TowerAttackResultDto>> fetchAllByGodId(ObjectId godId, Pageable pageable);
}
