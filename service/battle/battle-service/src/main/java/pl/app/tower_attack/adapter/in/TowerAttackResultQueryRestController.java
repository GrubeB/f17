package pl.app.tower_attack.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.tower_attack.application.domain.TowerAttackException;
import pl.app.tower_attack.query.TowerAttackResultQueryService;
import pl.app.tower_attack.query.dto.TowerAttackResultDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(TowerAttackResultQueryRestController.resourcePath)
@RequiredArgsConstructor
class TowerAttackResultQueryRestController {
    public static final String resourceName = "tower-attack-results";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final TowerAttackResultQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<TowerAttackResultDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(TowerAttackException.NotFoundTowerAttackResultException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<TowerAttackResultDto>>> fetchAllByGodIds(Pageable pageable,
                                                                      @RequestParam(name = "ids", required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }
}
