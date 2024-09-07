package pl.app.monster_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.monster_template.application.domain.MonsterTemplateException;
import pl.app.monster_template.dto.MonsterTemplateDto;
import pl.app.monster_template.query.MonsterTemplateQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(MonsterTemplatesQueryRestController.resourcePath)
@RequiredArgsConstructor
class MonsterTemplatesQueryRestController {
    public static final String resourceName = "monster-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final MonsterTemplateQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<MonsterTemplateDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(MonsterTemplateException.NotFoundMonsterTemplateException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<MonsterTemplateDto>>> fetchAllByIds(Pageable pageable, @RequestParam(required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
