package pl.app.battle.application.port.out;

import org.bson.types.ObjectId;
import pl.app.battle.application.domain.battle.BattleCharacter;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface CharacterRepository {
    Mono<BattleCharacter> getBattleCharacterById(ObjectId id);

    Mono<Set<BattleCharacter>> getBattleCharacterByGodId(ObjectId godId, Set<ObjectId> characterIds);
}
