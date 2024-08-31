package pl.app.god_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god.application.domain.GodException;
import pl.app.god_template.application.domain.GodTemplateException;
import pl.app.god_template.query.GodTemplateQueryService;
import pl.app.god_template.query.dto.GodTemplateDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(GodTemplatesQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodTemplatesQueryRestController {
    public static final String resourceName = "god-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodTemplateQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<GodTemplateDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(GodTemplateException.NotFoundGodTemplateException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<GodTemplateDto>>> fetchAllByIds(Pageable pageable, @RequestParam(required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
