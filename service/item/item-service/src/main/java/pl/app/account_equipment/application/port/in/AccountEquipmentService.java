package pl.app.account_equipment.application.port.in;

import pl.app.account_equipment.application.domain.AccountEquipment;
import reactor.core.publisher.Mono;

public interface AccountEquipmentService {
    Mono<AccountEquipment> createAccountEquipment(AccountEquipmentCommand.CreateAccountEquipmentCommand command);
    Mono<AccountEquipment> addItemToAccountEquipment(AccountEquipmentCommand.AddItemToAccountEquipmentCommand command);
    Mono<AccountEquipment> removeItemToAccountEquipment(AccountEquipmentCommand.RemoveItemToAccountEquipmentCommand command);
    Mono<AccountEquipment> setCharacterItem(AccountEquipmentCommand.SetCharacterItemCommand command);
    Mono<AccountEquipment> removeCharacterItem(AccountEquipmentCommand.RemoveCharacterItemCommand command);
}
