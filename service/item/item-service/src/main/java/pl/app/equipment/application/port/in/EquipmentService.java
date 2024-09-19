package pl.app.equipment.application.port.in;

import pl.app.equipment.application.domain.Equipment;
import reactor.core.publisher.Mono;

public interface EquipmentService {
    Mono<Equipment> createEquipment(EquipmentCommand.CreateGodEquipmentCommand command);

    Mono<Equipment> addCharacterGearToGodEquipment(EquipmentCommand.AddCharacterGearToGodEquipmentCommand command);

    Mono<Equipment> removeCharacterGearFromGodEquipment(EquipmentCommand.RemoveCharacterGearFromGodEquipmentCommand command);

    Mono<Equipment> addItemToEquipment(EquipmentCommand.AddItemToGodEquipmentCommand command);

    Mono<Equipment> removeItemFromEquipment(EquipmentCommand.RemoveItemFromGodEquipmentCommand command);
}
