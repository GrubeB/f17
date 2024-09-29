package pl.app.character_status.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.character.application.domain.CharacterException;
import pl.app.character.query.dto.CharacterDto;
import pl.app.character_status.query.CharacterStatusQueryService;
import pl.app.character_status.query.dto.CharacterStatusDto;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(CharacterStatusQueryRestController.resourcePath)
@RequiredArgsConstructor
class CharacterStatusQueryRestController {
    public static final String resourceName = "statuses";
    public static final String resourcePath = "/api/v1/characters/{characterId}/" + resourceName;

    private final CharacterStatusQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<CharacterStatusDto>> fetchByCharacterId(@PathVariable ObjectId characterId) {
        return queryService.fetchByCharacterId(characterId)
                .map(ResponseEntity::ok);
    }
}
