package pl.app.gear.application.port.in;

import org.bson.types.ObjectId;
import pl.app.gear.aplication.domain.Gear;
import reactor.core.publisher.Mono;

public interface GearDomainRepository {
    Mono<Gear> fetchByDomainObject(ObjectId domainObjectId, Gear.LootDomainObjectType domainObjectType);
}
