package pl.app.tower.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.monster_template.application.domain.MonsterTemplateException;
import pl.app.monster_template.dto.MonsterTemplateDto;
import pl.app.monster_template.query.MonsterTemplateQueryService;
import pl.app.tower.application.domain.TowerException;
import pl.app.tower.dto.TowerLevelDto;
import pl.app.tower.query.TowerLevelQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(TowerLevelQueryRestController.resourcePath)
@RequiredArgsConstructor
class TowerLevelQueryRestController {
    public static final String resourceName = "tower-levels";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final TowerLevelQueryService queryService;

    @GetMapping("/{level}")
    Mono<ResponseEntity<TowerLevelDto>> fetchByLevel(
            @PathVariable Integer level
    ) {
        return queryService.fetchByLevel(level)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(TowerException.NotFoundTowerLevelException.fromLevel(level)));
    }

    @GetMapping
    Mono<ResponseEntity<Page<TowerLevelDto>>> fetchAll() {
        return queryService.fetchAll()
                .map(ResponseEntity::ok);
    }

}
