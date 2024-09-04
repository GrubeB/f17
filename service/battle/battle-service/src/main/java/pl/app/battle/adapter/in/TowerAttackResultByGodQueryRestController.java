package pl.app.battle.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.battle.application.domain.TowerAttackException;
import pl.app.battle.query.TowerAttackResultQueryService;
import pl.app.battle.query.dto.TowerAttackResultDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(TowerAttackResultByGodQueryRestController.resourcePath)
@RequiredArgsConstructor
class TowerAttackResultByGodQueryRestController {
    public static final String resourceName = "tower-attack-results";
    public static final String resourcePath = "/api/v1/gods/{godId}/" + resourceName;

    private final TowerAttackResultQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<TowerAttackResultDto>> fetchById(
            @PathVariable ObjectId godId,
            @PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(TowerAttackException.NotFoundTowerAttackResultException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<TowerAttackResultDto>>> fetchAllByGodIds(
            Pageable pageable,
            @PathVariable ObjectId godId) {
        return queryService.fetchAllByGodId(godId, pageable)
                .map(ResponseEntity::ok);

    }
}
