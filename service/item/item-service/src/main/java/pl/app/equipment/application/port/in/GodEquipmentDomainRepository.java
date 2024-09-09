package pl.app.equipment.application.port.in;

import org.bson.types.ObjectId;
import pl.app.equipment.application.domain.Equipment;
import reactor.core.publisher.Mono;

public interface GodEquipmentDomainRepository {
    Mono<Equipment> fetchByGodId(ObjectId godId);
}
