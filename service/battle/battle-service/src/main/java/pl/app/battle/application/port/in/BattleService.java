package pl.app.battle.application.port.in;

import pl.app.battle.application.domain.TowerAttackResult;
import pl.app.battle.application.domain.BattleResult;
import reactor.core.publisher.Mono;

public interface BattleService {
    Mono<BattleResult> startDuelBattle(BattleCommand.StartDuelBattleCommand command);
    Mono<BattleResult> startTwoGodBattle(BattleCommand.StartTwoGodBattleCommand command);
    Mono<TowerAttackResult> attackTower(BattleCommand.AttackTowerCommand command);
}
