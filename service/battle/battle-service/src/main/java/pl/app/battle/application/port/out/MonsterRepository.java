package pl.app.battle.application.port.out;

import org.bson.types.ObjectId;
import pl.app.battle.application.domain.BattleCharacter;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface MonsterRepository {
    Mono<Set<BattleCharacter>> getByTowerLevel(Integer level);
}
