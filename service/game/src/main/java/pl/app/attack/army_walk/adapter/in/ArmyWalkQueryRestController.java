package pl.app.attack.army_walk.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.attack.army_walk.domain.application.ArmyWalkException;
import pl.app.attack.army_walk.query.ArmyWalkDtoQueryService;
import pl.app.attack.army_walk.query.dto.ArmyWalkDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ArmyWalkQueryRestController.resourcePath)
@RequiredArgsConstructor
class ArmyWalkQueryRestController {
    public static final String resourceName = "army-walks";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ArmyWalkDtoQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<ArmyWalkDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(ArmyWalkException.NotFoundArmyWalkException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Flux<ArmyWalkDto>>> fetchAll() {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll()));
    }
}
