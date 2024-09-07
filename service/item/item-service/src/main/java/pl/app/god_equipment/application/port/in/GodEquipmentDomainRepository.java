package pl.app.god_equipment.application.port.in;

import org.bson.types.ObjectId;
import pl.app.god_equipment.application.domain.CharacterGear;
import pl.app.god_equipment.application.domain.GodEquipment;
import reactor.core.publisher.Mono;

public interface GodEquipmentDomainRepository {
    Mono<GodEquipment> fetchByGodId(ObjectId godId);
    Mono<CharacterGear> fetchCharacterGearByCharacterId(ObjectId characterId);
}
