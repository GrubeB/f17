package pl.app.family.application.port.out;

import pl.app.family.application.domain.Family;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface FamilyDomainRepository {
    Mono<Family> fetchByGodId(ObjectId godId);
}
