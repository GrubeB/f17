package pl.app.village.village.application.port.in;

import org.bson.types.ObjectId;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.village.village.application.domain.Village;
import reactor.core.publisher.Mono;

public interface VillageDomainRepository {
    Mono<Village> fetchById(ObjectId villageId);
}
