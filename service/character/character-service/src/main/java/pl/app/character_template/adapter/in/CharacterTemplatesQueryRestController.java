package pl.app.character_template.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.character_template.application.domain.CharacterTemplateException;
import pl.app.character_template.dto.CharacterTemplateDto;
import pl.app.character_template.query.CharacterTemplateQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(CharacterTemplatesQueryRestController.resourcePath)
@RequiredArgsConstructor
class CharacterTemplatesQueryRestController {
    public static final String resourceName = "character-templates";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CharacterTemplateQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<CharacterTemplateDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(CharacterTemplateException.NotFoundCharacterTemplateException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<CharacterTemplateDto>>> fetchAllByIds(Pageable pageable, @RequestParam(required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
