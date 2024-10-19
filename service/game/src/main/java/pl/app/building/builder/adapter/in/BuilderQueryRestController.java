package pl.app.building.builder.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.building.builder.application.domain.BuilderException;
import pl.app.building.builder.query.BuilderDtoQueryService;
import pl.app.building.builder.query.dto.BuilderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(BuilderQueryRestController.resourcePath)
@RequiredArgsConstructor
class BuilderQueryRestController {
    public static final String resourceName = "builders";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final BuilderDtoQueryService queryService;

    @GetMapping("/{villageId}")
    Mono<ResponseEntity<BuilderDto>> fetchByVillageId(@PathVariable ObjectId villageId) {
        return queryService.fetchByVillageId(villageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(BuilderException.NotFoundBuilderException.fromId(villageId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Flux<BuilderDto>>> fetchByVillageId() {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll()));
    }
}
