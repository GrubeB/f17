package pl.app.loot.application.port.in;

import org.bson.types.ObjectId;
import pl.app.loot.aplication.domain.Loot;
import reactor.core.publisher.Mono;

public interface LootDomainRepository {
    Mono<Loot> fetchByDomainObject(ObjectId domainObjectId, Loot.LootDomainObjectType domainObjectType);
}
