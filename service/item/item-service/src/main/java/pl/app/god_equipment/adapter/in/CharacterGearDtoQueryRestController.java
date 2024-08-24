package pl.app.god_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_equipment.application.domain.GodEquipmentException;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.god_equipment.query.CharacterGearDtoQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(CharacterGearDtoQueryRestController.resourcePath)
@RequiredArgsConstructor
class CharacterGearDtoQueryRestController {
    public static final String resourceName = "character-gears";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CharacterGearDtoQueryService queryService;

    @GetMapping("/{characterId}")
    Mono<ResponseEntity<CharacterGearDto>> fetchByCharacterId(@PathVariable ObjectId characterId) {
        return queryService.fetchByCharacterId(characterId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(GodEquipmentException.NotFoundGodEquipmentException.fromGodId(characterId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<CharacterGearDto>>> fetchAllByCharacterIds(Pageable pageable, @RequestParam List<ObjectId> ids) {
        return queryService.fetchAllByCharacterIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
