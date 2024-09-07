package pl.app.tower.application.port.in;

import org.bson.types.ObjectId;
import pl.app.monster_template.application.domain.MonsterTemplate;
import pl.app.tower.application.domain.TowerLevel;
import reactor.core.publisher.Mono;

public interface TowerLevelDomainRepository {
    Mono<TowerLevel> fetchByLevel(Integer level);
}
