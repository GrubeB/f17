package pl.app.resource.village_resource.application.port.in;

import org.bson.types.ObjectId;
import pl.app.map.village_position.application.domain.VillagePosition;
import pl.app.resource.village_resource.application.domain.VillageResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VillageResourceDomainRepository {
    Mono<VillageResource> fetchByVillageId(ObjectId villageId);
}
