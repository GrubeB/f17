package pl.app.monster.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.monster.application.domain.MonsterException;
import pl.app.monster.query.MonsterQueryService;
import pl.app.monster.query.dto.MonsterDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(MonsterQueryRestController.resourcePath)
@RequiredArgsConstructor
class MonsterQueryRestController {
    public static final String resourceName = "monsters";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final MonsterQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<MonsterDto>> fetchById(
            @PathVariable ObjectId id
    ) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(MonsterException.NotFoundMonsterException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<MonsterDto>>> fetchAllByIds(
            Pageable pageable,
            @RequestParam(required = false) List<ObjectId> ids
    ) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
