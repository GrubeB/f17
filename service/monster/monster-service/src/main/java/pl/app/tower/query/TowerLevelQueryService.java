package pl.app.tower.query;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import pl.app.tower.dto.TowerLevelDto;
import reactor.core.publisher.Mono;

public interface TowerLevelQueryService {
    Mono<TowerLevelDto> fetchByLevel(@NonNull Integer id);

    Mono<Page<TowerLevelDto>> fetchAll();
}
