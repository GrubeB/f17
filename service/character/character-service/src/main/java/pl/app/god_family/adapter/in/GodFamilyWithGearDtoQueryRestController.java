package pl.app.god_family.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_family.query.GodFamilyWithGearDtoQueryService;
import pl.app.god_family.query.dto.GodFamilyWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(GodFamilyWithGearDtoQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodFamilyWithGearDtoQueryRestController {
    public static final String resourceName = "god-families-with-gears";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodFamilyWithGearDtoQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<GodFamilyWithGearDto>>> fetchAllByIds(Pageable pageable, @RequestParam(required = false) List<ObjectId> godIds) {
        return queryService.fetchAllByGodIds(godIds, pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{godId}")
    Mono<ResponseEntity<GodFamilyWithGearDto>> fetchById(@PathVariable ObjectId godId) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
