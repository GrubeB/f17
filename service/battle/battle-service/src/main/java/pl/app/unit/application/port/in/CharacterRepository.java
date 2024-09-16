package pl.app.unit.application.port.in;

import org.bson.types.ObjectId;
import pl.app.unit.application.domain.BattleCharacter;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface CharacterRepository {
    Mono<BattleCharacter> getBattleCharacterById(ObjectId id);

    Mono<Set<BattleCharacter>> getBattleCharacterByGodId(ObjectId godId, Set<ObjectId> characterIds);
}
