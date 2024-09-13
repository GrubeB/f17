package pl.app.battle.application.port.out;

import pl.app.battle.application.domain.battle.BattleCharacter;
import pl.app.tower.dto.TowerLevelDto;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface TowerRepository {
    Mono<TowerLevelDto> getTowerLevel(Integer level);
}
