package pl.app.god_family.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.god_family.query.GodFamilyQueryService;
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodFamilyQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodFamilyQueryRestController {
    public static final String resourceName = "god-families";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodFamilyQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<GodFamilyDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{godId}")
    Mono<ResponseEntity<GodFamilyDto>> fetchById(@PathVariable ObjectId godId) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
