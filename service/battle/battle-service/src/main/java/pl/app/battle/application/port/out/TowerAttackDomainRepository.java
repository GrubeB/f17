package pl.app.battle.application.port.out;

import org.bson.types.ObjectId;
import pl.app.battle.application.domain.battle.BattleCharacter;
import pl.app.battle.application.domain.tower_attack.TowerAttack;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface TowerAttackDomainRepository {
    TowerAttack getTowerAttack(ObjectId id);
    TowerAttack save(TowerAttack domain);
    void remove(ObjectId id);
}
