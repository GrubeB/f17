package pl.app.army_walk.army_walk.domain.port.in;

import pl.app.army_walk.army_walk.domain.application.ArmyWalk;
import reactor.core.publisher.Mono;

public interface ArmyWalkService {
    Mono<ArmyWalk> send(ArmyWalkCommand.SendArmyCommand command);

    Mono<ArmyWalk> cancel(ArmyWalkCommand.CancelArmyCommand command);

    Mono<ArmyWalk> process(ArmyWalkCommand.ProcessArmyArrivalCommand command);
}
