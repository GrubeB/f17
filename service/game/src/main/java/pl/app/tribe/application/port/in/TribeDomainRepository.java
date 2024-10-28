package pl.app.tribe.application.port.in;

import org.bson.types.ObjectId;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.tribe.application.domain.Tribe;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface TribeDomainRepository {
    Mono<Tribe> fetchById(ObjectId tribeId);
}
