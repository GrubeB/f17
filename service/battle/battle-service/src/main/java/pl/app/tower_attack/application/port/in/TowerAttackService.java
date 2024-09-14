package pl.app.tower_attack.application.port.in;

import pl.app.tower_attack.application.domain.TowerAttack;
import reactor.core.publisher.Mono;

public interface TowerAttackService {
    Mono<TowerAttack> attackTower(TowerAttackCommand.AttackTowerCommand command);
}
