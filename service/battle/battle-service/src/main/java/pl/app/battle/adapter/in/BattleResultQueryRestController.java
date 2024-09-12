package pl.app.battle.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.battle.application.domain.battle.BattleException;
import pl.app.battle.query.BattleResultQueryService;
import pl.app.battle.query.dto.BattleResultDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(BattleResultQueryRestController.resourcePath)
@RequiredArgsConstructor
class BattleResultQueryRestController {
    public static final String resourceName = "battle-results";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final BattleResultQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<BattleResultDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(BattleException.NotFoundBattleException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<BattleResultDto>>> fetchAllByGodIds(Pageable pageable, @RequestParam(name = "ids", required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }
}
