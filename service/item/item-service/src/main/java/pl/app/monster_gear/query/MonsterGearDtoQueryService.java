package pl.app.monster_gear.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import pl.app.monster_gear.dto.MonsterGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MonsterGearDtoQueryService {
    Mono<MonsterGearDto> fetchByMonsterId(@NonNull ObjectId monsterId);
    Mono<Page<MonsterGearDto>> fetchAllByPageable(Pageable pageable);
    Mono<Page<MonsterGearDto>> fetchAllByMonsterIds(List<ObjectId> monsterIds, Pageable pageable);
}
