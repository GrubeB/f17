package pl.app.tower_attack.application.port.out;

import pl.app.tower.dto.TowerLevelDto;
import reactor.core.publisher.Mono;

public interface TowerRepository {
    Mono<TowerLevelDto> getTowerLevel(Integer level);
}
