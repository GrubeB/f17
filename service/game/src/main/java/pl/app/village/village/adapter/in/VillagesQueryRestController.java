package pl.app.village.village.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.village.village.application.domain.VillageException;
import pl.app.village.village.query.VillageDtoQueryService;
import pl.app.village.village.query.dto.VillageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VillagesQueryRestController.resourcePath)
@RequiredArgsConstructor
class VillagesQueryRestController {
    public static final String resourceName = "villages";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final VillageDtoQueryService queryService;

    @GetMapping("/{villageId}")
    Mono<ResponseEntity<VillageDto>> fetchById(@PathVariable ObjectId villageId) {
        return queryService.fetchById(villageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(VillageException.NotFoundVillageException.fromId(villageId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Flux<VillageDto>>> fetchAll() {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll()));
    }
}
