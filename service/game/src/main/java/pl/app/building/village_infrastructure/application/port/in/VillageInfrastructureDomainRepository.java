package pl.app.building.village_infrastructure.application.port.in;

import org.bson.types.ObjectId;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructure;
import reactor.core.publisher.Mono;

public interface VillageInfrastructureDomainRepository {
    Mono<VillageInfrastructure> fetchByVillageId(ObjectId villageId);
}
