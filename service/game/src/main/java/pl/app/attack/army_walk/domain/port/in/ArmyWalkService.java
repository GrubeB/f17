package pl.app.attack.army_walk.domain.port.in;

import pl.app.attack.army_walk.domain.application.ArmyWalk;
import reactor.core.publisher.Mono;

public interface ArmyWalkService {
    Mono<ArmyWalk> sendArmy(ArmyWalkCommand.SendArmyCommand command);

    Mono<ArmyWalk> process(ArmyWalkCommand.ProcessArmyArrivalCommand command);
}
