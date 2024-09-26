package pl.app.equipment.application.port.in;

import pl.app.equipment.application.domain.Equipment;
import reactor.core.publisher.Mono;

public interface EquipmentService {
    Mono<Equipment> createEquipment(EquipmentCommand.CreateEquipmentCommand command);

    Mono<Equipment> addCharacterGearToEquipment(EquipmentCommand.AddCharacterGearToEquipmentCommand command);

    Mono<Equipment> removeCharacterGearFromEquipment(EquipmentCommand.RemoveCharacterGearFromEquipmentCommand command);

    Mono<Equipment> addItemToEquipment(EquipmentCommand.AddItemToEquipmentCommand command);

    Mono<Equipment> removeItemFromEquipment(EquipmentCommand.RemoveItemFromEquipmentCommand command);
}
