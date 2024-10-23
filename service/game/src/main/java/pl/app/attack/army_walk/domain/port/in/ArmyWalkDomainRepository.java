package pl.app.attack.army_walk.domain.port.in;

import org.bson.types.ObjectId;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface ArmyWalkDomainRepository {
    Mono<ArmyWalk> fetchById(ObjectId armyWalkId);

    Flux<ArmyWalk> fetchArmyWalkWithEnding(Duration withinTime);
}
