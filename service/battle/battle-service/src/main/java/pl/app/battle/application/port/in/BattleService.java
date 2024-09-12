package pl.app.battle.application.port.in;

import pl.app.battle.application.domain.tower_attack.TowerAttack;
import pl.app.battle.application.domain.tower_attack.TowerAttackResult;
import pl.app.battle.application.domain.battle.BattleResult;
import reactor.core.publisher.Mono;

public interface BattleService {
    Mono<BattleResult> startDuelBattle(BattleCommand.StartDuelBattleCommand command);
    Mono<BattleResult> startTwoGodBattle(BattleCommand.StartTwoGodBattleCommand command);
    Mono<TowerAttack> attackTower(BattleCommand.AttackTowerCommand command);
}
