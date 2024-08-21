package pl.app.battle.application.port.out;

import org.bson.types.ObjectId;
import pl.app.battle.application.domain.BattleCharacter;
import reactor.core.publisher.Mono;

public interface CharacterRepository {
    Mono<BattleCharacter> getBattleCharacterById(ObjectId id);
}
