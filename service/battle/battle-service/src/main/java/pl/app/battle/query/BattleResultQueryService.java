package pl.app.battle.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.battle.query.dto.BattleResultDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BattleResultQueryService {
    Mono<BattleResultDto> fetchById(@NonNull ObjectId id);

    Mono<Page<BattleResultDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<BattleResultDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable);
}
