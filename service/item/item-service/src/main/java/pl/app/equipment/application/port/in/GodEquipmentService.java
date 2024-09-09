package pl.app.equipment.application.port.in;

import pl.app.equipment.application.domain.Equipment;
import reactor.core.publisher.Mono;

public interface GodEquipmentService {
    Mono<Equipment> createEquipment(GodEquipmentCommand.CreateGodEquipmentCommand command);

    Mono<Equipment> addCharacterGearToGodEquipment(GodEquipmentCommand.AddCharacterGearToGodEquipmentCommand command);

    Mono<Equipment> removeCharacterGearFromGodEquipment(GodEquipmentCommand.RemoveCharacterGearFromGodEquipmentCommand command);

    Mono<Equipment> addItemToEquipment(GodEquipmentCommand.AddItemToGodEquipmentCommand command);

    Mono<Equipment> removeItemFromEquipment(GodEquipmentCommand.RemoveItemFromGodEquipmentCommand command);
}
