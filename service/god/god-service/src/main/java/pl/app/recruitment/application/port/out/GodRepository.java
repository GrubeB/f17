package pl.app.recruitment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.god.application.domain.God;
import reactor.core.publisher.Mono;

public interface GodRepository {
    Mono<God> fetchById(ObjectId id);
}
