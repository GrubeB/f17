package pl.app.character.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.character.application.domain.CharacterException;
import pl.app.character.query.CharacterQueryService;
import pl.app.character.query.dto.CharacterDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(CharacterQueryRestController.resourcePath)
@RequiredArgsConstructor
class CharacterQueryRestController {
    public static final String resourceName = "characters";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CharacterQueryService queryService;

    @GetMapping("/{id}")
    Mono<ResponseEntity<CharacterDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(CharacterException.NotFoundCharacterException.fromId(id.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<CharacterDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchAllByPageable(pageable)
                .map(ResponseEntity::ok);
    }

}
