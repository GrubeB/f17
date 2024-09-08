package pl.app.loot.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.aplication.domain.LootException;
import pl.app.loot.dto.LootDto;
import pl.app.loot.query.LootDtoQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(LootDtoQueryRestController.resourcePath)
@RequiredArgsConstructor
class LootDtoQueryRestController {
    public static final String resourceName = "loots";
    public static final String resourcePath = "/api/v1/domainObjectTypes/{domainObjectType}/" + resourceName;

    private final LootDtoQueryService queryService;

    @GetMapping("/{domainObjectId}")
    Mono<ResponseEntity<LootDto>> fetchByDomainObject(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Loot.LootDomainObjectType domainObjectType
    ) {
        return queryService.fetchByDomainObject(domainObjectId, domainObjectType)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(LootException.NotFoundLootException.fromId(domainObjectId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<LootDto>>> fetchByDomainObject(
            @PathVariable Loot.LootDomainObjectType domainObjectType,
            Pageable pageable,
            @RequestParam(required = false) List<ObjectId> ids
    ) {
        return queryService.fetchAllByDomainObjectIds(ids, domainObjectType, pageable)
                .map(ResponseEntity::ok);
    }
}
