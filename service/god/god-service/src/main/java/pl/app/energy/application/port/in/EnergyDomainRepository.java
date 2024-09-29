package pl.app.energy.application.port.in;

import org.bson.types.ObjectId;
import pl.app.energy.application.domain.Energy;
import pl.app.god.application.domain.God;
import reactor.core.publisher.Mono;

public interface EnergyDomainRepository {
    Mono<Energy> fetchByGodId(ObjectId godId);
}
