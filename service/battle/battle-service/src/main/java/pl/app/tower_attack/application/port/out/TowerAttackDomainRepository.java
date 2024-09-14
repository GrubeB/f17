package pl.app.tower_attack.application.port.out;

import org.bson.types.ObjectId;
import pl.app.tower_attack.application.domain.TowerAttack;

public interface TowerAttackDomainRepository {
    TowerAttack getTowerAttack(ObjectId id);

    TowerAttack save(TowerAttack domain);

    void remove(ObjectId id);
}
