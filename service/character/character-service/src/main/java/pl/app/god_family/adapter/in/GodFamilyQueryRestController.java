package pl.app.god_family.adapter.in;

import pl.app.god_family.application.domain.GodFamilyException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_family.query.GodFamilyQueryService;
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(GodFamilyQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodFamilyQueryRestController {
    public static final String resourceName = "god-families";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodFamilyQueryService queryService;

    @GetMapping("/{godId}")
    Mono<ResponseEntity<GodFamilyDto>> fetchByGodId(@PathVariable ObjectId godId) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(GodFamilyException.NotFoundGodFamilyException.fromGodId(godId.toHexString())));
    }
    @GetMapping
    Mono<ResponseEntity<Page<GodFamilyDto>>> fetchAllByGodIds(Pageable pageable, @RequestParam(name = "ids",required = false) List<ObjectId> godIds) {
        return queryService.fetchAllByGodIds(godIds, pageable)
                .map(ResponseEntity::ok);
    }
}
