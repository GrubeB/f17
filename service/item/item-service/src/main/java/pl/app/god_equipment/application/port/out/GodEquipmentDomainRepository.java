package pl.app.god_equipment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.god_equipment.application.domain.GodEquipment;
import reactor.core.publisher.Mono;

public interface GodEquipmentDomainRepository {
    Mono<GodEquipment> fetchByAccountId(ObjectId godId);
}
