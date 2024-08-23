package pl.app.god_equipment.application.port.in;

import pl.app.god_equipment.application.domain.GodEquipment;
import reactor.core.publisher.Mono;

public interface GodEquipmentService {
    Mono<GodEquipment> createEquipment(GodEquipmentCommand.CreateGodEquipmentCommand command);

    Mono<GodEquipment> addItemToEquipment(GodEquipmentCommand.AddItemToGodEquipmentCommand command);

    Mono<GodEquipment> removeItemFromEquipment(GodEquipmentCommand.RemoveItemFromGodEquipmentCommand command);

    Mono<GodEquipment> setCharacterItem(GodEquipmentCommand.SetCharacterItemCommand command);

    Mono<GodEquipment> removeCharacterItem(GodEquipmentCommand.RemoveCharacterItemCommand command);
}
