package pl.app.family.adapter.in;

import pl.app.family.application.domain.FamilyException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.family.query.FamilyQueryService;
import pl.app.family.query.dto.FamilyDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(FamilyQueryRestController.resourcePath)
@RequiredArgsConstructor
class FamilyQueryRestController {
    public static final String resourceName = "families";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final FamilyQueryService queryService;

    @GetMapping("/{godId}")
    Mono<ResponseEntity<FamilyDto>> fetchByGodId(@PathVariable ObjectId godId) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(FamilyException.NotFoundGodFamilyException.fromGodId(godId.toHexString())));
    }
    @GetMapping
    Mono<ResponseEntity<Page<FamilyDto>>> fetchAllByGodIds(Pageable pageable, @RequestParam(name = "ids",required = false) List<ObjectId> godIds) {
        return queryService.fetchAllByGodIds(godIds, pageable)
                .map(ResponseEntity::ok);
    }
}
