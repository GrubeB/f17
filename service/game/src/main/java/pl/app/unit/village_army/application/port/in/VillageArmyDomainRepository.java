package pl.app.unit.village_army.application.port.in;

import org.bson.types.ObjectId;
import pl.app.unit.village_army.application.domain.VillageArmy;
import reactor.core.publisher.Mono;

public interface VillageArmyDomainRepository {
    Mono<VillageArmy> fetchByVillageId(ObjectId villageId);
}
