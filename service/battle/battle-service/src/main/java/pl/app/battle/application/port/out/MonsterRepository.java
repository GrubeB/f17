package pl.app.battle.application.port.out;

import pl.app.battle.application.domain.battle.BattleCharacter;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface MonsterRepository {
    Mono<Set<BattleCharacter>> getByTowerLevel(Integer level);
}
