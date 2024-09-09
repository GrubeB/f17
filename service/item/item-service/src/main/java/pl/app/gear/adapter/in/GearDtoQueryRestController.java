package pl.app.gear.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.aplication.domain.GearException;
import pl.app.gear.dto.GearDto;
import pl.app.gear.query.GearDtoQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(GearDtoQueryRestController.resourcePath)
@RequiredArgsConstructor
class GearDtoQueryRestController {
    public static final String resourceName = "gears";
    public static final String resourcePath = "/api/v1/domainObjectTypes/{domainObjectType}/" + resourceName;

    private final GearDtoQueryService queryService;

    @GetMapping("/{domainObjectId}")
    Mono<ResponseEntity<GearDto>> fetchByDomainObject(
            @PathVariable ObjectId domainObjectId,
            @PathVariable Gear.LootDomainObjectType domainObjectType
    ) {
        return queryService.fetchByDomainObject(domainObjectId, domainObjectType)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(GearException.NotFoundGearException.fromId(domainObjectId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<GearDto>>> fetchByDomainObject(
            @PathVariable Gear.LootDomainObjectType domainObjectType,
            Pageable pageable,
            @RequestParam(required = false) List<ObjectId> ids
    ) {
        return queryService.fetchAllByDomainObjectIds(ids, domainObjectType, pageable)
                .map(ResponseEntity::ok);
    }

}
