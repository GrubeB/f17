package pl.app.god_family.application.port.out;

import pl.app.god_family.application.domain.GodFamily;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface GodFamilyDomainRepository {
    Mono<GodFamily> fetchByGodId(ObjectId godId);
}
