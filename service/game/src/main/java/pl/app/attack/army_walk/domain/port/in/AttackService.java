package pl.app.attack.army_walk.domain.port.in;

import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.application.Attack;
import reactor.core.publisher.Mono;

public interface AttackService {
    Mono<ArmyWalk> sendArmy(AttackCommand.SendArmyCommand command);
    Mono<Attack> process(AttackCommand.ProcessArmyArrivalCommand command);
}
