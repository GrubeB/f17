package pl.app.loot.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.application.port.in.LootCommand;
import pl.app.loot.application.port.in.LootService;
import pl.app.loot.dto.LootDto;
import pl.app.loot.query.LootDtoQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(LootRestController.resourcePath)
@RequiredArgsConstructor
class LootRestController {
    public static final String resourceName = "loots";
    public static final String resourcePath = "/api/v1/domainObjectTypes/{domainObjectType}/" + resourceName;

    private final LootService service;
    private final LootDtoQueryService queryService;


    @PostMapping("/{domainObjectId}/items")
    Mono<ResponseEntity<LootDto>> setItem(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Loot.LootDomainObjectType domainObjectType,
            @RequestBody LootCommand.SetItemCommand command
    ) {
        return service.setItem(command)
                .flatMap(domain -> queryService.fetchByDomainObject(domain.getDomainObjectId(), domain.getDomainObjectType()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{domainObjectId}/items")
    Mono<ResponseEntity<LootDto>> removeItem(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Loot.LootDomainObjectType domainObjectType,
            @RequestBody LootCommand.RemoveItemCommand command
    ) {
        return service.removeItem(command)
                .map(unused -> ResponseEntity.accepted().build());
    }
}
