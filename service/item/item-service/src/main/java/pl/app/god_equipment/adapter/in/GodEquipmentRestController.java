package pl.app.god_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_equipment.application.port.in.GodEquipmentCommand;
import pl.app.god_equipment.application.port.in.GodEquipmentService;
import pl.app.god_equipment.dto.GodEquipmentDto;
import pl.app.god_equipment.query.GodEquipmentQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodEquipmentRestController.resourcePath)
@RequiredArgsConstructor
class GodEquipmentRestController {
    public static final String resourceName = "god-equipments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodEquipmentService service;
    private final GodEquipmentQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<GodEquipmentDto>> createEquipment(@RequestBody GodEquipmentCommand.CreateGodEquipmentCommand command) {
        return service.createEquipment(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{godId}/items")
    public Mono<ResponseEntity<GodEquipmentDto>> addItemToEquipment(@PathVariable ObjectId godId, @RequestBody GodEquipmentCommand.AddItemToGodEquipmentCommand command) {
        command.setGodId(godId);
        return service.addItemToEquipment(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{godId}/items")
    public Mono<ResponseEntity<GodEquipmentDto>> removeItemFromEquipment(@PathVariable ObjectId godId, @RequestBody GodEquipmentCommand.RemoveItemFromGodEquipmentCommand command) {
        command.setGodId(godId);
        return service.removeItemFromEquipment(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{godId}/characters/{characterId}/items")
    public Mono<ResponseEntity<GodEquipmentDto>> setCharacterItem(@PathVariable ObjectId godId, @PathVariable ObjectId characterId,
                                                                  @RequestBody GodEquipmentCommand.SetCharacterItemCommand command) {
        command.setGodId(godId);
        command.setCharacterId(characterId);
        return service.setCharacterItem(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{godId}/characters/{characterId}/items")
    public Mono<ResponseEntity<GodEquipmentDto>> removeCharacterItem(@PathVariable ObjectId godId, @PathVariable ObjectId characterId,
                                                                     @RequestBody GodEquipmentCommand.RemoveCharacterItemCommand command) {
        command.setGodId(godId);
        command.setCharacterId(characterId);
        return service.removeCharacterItem(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
