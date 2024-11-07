package pl.app.tribe.application.port.in;

import org.bson.types.ObjectId;
import pl.app.tribe.application.domain.Tribe;
import reactor.core.publisher.Mono;

public interface TribeDomainRepository {
    Mono<Tribe> fetchById(ObjectId tribeId);
}
