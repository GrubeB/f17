package pl.app.equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.equipment.application.port.in.GodEquipmentCommand;
import pl.app.equipment.application.port.in.GodEquipmentService;
import pl.app.equipment.dto.EquipmentDto;
import pl.app.equipment.query.EquipmentQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(EquipmentRestController.resourcePath)
@RequiredArgsConstructor
class EquipmentRestController {
    public static final String resourceName = "equipments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodEquipmentService service;
    private final EquipmentQueryService queryService;


    @PostMapping("/{godId}/items")
    Mono<ResponseEntity<EquipmentDto>> addItemToEquipment(
            @PathVariable ObjectId godId,
            @RequestBody GodEquipmentCommand.AddItemToGodEquipmentCommand command
    ) {
        command.setGodId(godId);
        return service.addItemToEquipment(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{godId}/items")
    Mono<ResponseEntity<EquipmentDto>> removeItemFromEquipment(
            @PathVariable ObjectId godId,
            @RequestBody GodEquipmentCommand.RemoveItemFromGodEquipmentCommand command
    ) {
        command.setGodId(godId);
        return service.removeItemFromEquipment(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
