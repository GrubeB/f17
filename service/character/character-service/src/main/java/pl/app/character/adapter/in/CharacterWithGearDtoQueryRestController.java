package pl.app.character.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.character.application.domain.CharacterException;
import pl.app.character.query.CharacterWithGearDtoQueryService;
import pl.app.character.query.dto.CharacterWithGearDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(CharacterWithGearDtoQueryRestController.resourcePath)
@RequiredArgsConstructor
class CharacterWithGearDtoQueryRestController {
    public static final String resourceName = "character-with-gears";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CharacterWithGearDtoQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<CharacterWithGearDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(CharacterException.NotFoundCharacterException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<CharacterWithGearDto>>> fetchAllByIds(Pageable pageable, @RequestParam(required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
