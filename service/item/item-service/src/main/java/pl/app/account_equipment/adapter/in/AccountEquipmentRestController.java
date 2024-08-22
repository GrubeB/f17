package pl.app.account_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.account_equipment.application.port.in.AccountEquipmentCommand;
import pl.app.account_equipment.application.port.in.AccountEquipmentService;
import pl.app.account_equipment.dto.AccountEquipmentDto;
import pl.app.account_equipment.query.AccountEquipmentQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AccountEquipmentRestController.resourcePath)
@RequiredArgsConstructor
class AccountEquipmentRestController {
    public static final String resourceName = "account-equipments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final AccountEquipmentService service;
    private final AccountEquipmentQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<AccountEquipmentDto>> createOutfit(@RequestBody AccountEquipmentCommand.CreateAccountEquipmentCommand command) {
        return service.createAccountEquipment(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{accountId}/items")
    public Mono<ResponseEntity<AccountEquipmentDto>> add(@PathVariable ObjectId accountId, @RequestBody AccountEquipmentCommand.AddItemToAccountEquipmentCommand command) {
        command.setAccountId(accountId);
        return service.addItemToAccountEquipment(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{accountId}/items")
    public Mono<ResponseEntity<AccountEquipmentDto>> remove(@PathVariable ObjectId accountId, @RequestBody AccountEquipmentCommand.RemoveItemToAccountEquipmentCommand command) {
        command.setAccountId(accountId);
        return service.removeItemToAccountEquipment(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{accountId}/characters/{characterId}/items")
    public Mono<ResponseEntity<AccountEquipmentDto>> set(@PathVariable ObjectId accountId, @PathVariable ObjectId characterId,
                                                         @RequestBody AccountEquipmentCommand.SetCharacterItemCommand command) {
        command.setAccountId(accountId);
        command.setCharacterId(characterId);
        return service.setCharacterItem(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{accountId}/characters/{characterId}/items")
    public Mono<ResponseEntity<AccountEquipmentDto>> remove(@PathVariable ObjectId accountId, @PathVariable ObjectId characterId,
                                                            @RequestBody AccountEquipmentCommand.RemoveCharacterItemCommand command) {
        command.setAccountId(accountId);
        command.setCharacterId(characterId);
        return service.removeCharacterItem(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
