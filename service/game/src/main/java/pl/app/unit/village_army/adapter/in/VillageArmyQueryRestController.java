package pl.app.unit.village_army.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.unit.village_army.application.domain.VillageArmyException;
import pl.app.unit.village_army.query.VillageArmyDtoQueryService;
import pl.app.unit.village_army.query.dto.VillageArmyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VillageArmyQueryRestController.resourcePath)
@RequiredArgsConstructor
class VillageArmyQueryRestController {
    public static final String resourceName = "village-armies";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final VillageArmyDtoQueryService queryService;

    @GetMapping("/{villageId}")
    Mono<ResponseEntity<VillageArmyDto>> fetchByVillageId(@PathVariable ObjectId villageId) {
        return queryService.fetchByVillageId(villageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(VillageArmyException.NotFoundVillageArmyException.fromId(villageId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Flux<VillageArmyDto>>> fetchAll() {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll()));
    }
}
