package pl.app.gear.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.application.port.in.GearCommand;
import pl.app.gear.application.port.in.GearService;
import pl.app.gear.dto.GearDto;
import pl.app.gear.query.GearDtoQueryService;
import pl.app.loot.aplication.domain.Loot;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GearRestController.resourcePath)
@RequiredArgsConstructor
class GearRestController {
    public static final String resourceName = "gears";
    public static final String resourcePath = "/api/v1/domainObjectTypes/{domainObjectType}/" + resourceName;

    private final GearService service;
    private final GearDtoQueryService queryService;

    @PostMapping("/{domainObjectId}/items")
    Mono<ResponseEntity<GearDto>> setItem(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Gear.LootDomainObjectType domainObjectType,
            @RequestBody GearCommand.SetItemCommand command
    ) {
        return service.setItem(command)
                .flatMap(domain -> queryService.fetchByDomainObject(domain.getDomainObjectId(), domain.getDomainObjectType()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{domainObjectId}/items")
    Mono<ResponseEntity<GearDto>> removeItem(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Gear.LootDomainObjectType domainObjectType,
            @RequestBody GearCommand.RemoveItemCommand command
    ) {
        return service.removeItem(command)
                .map(unused -> ResponseEntity.accepted().build());
    }
}
