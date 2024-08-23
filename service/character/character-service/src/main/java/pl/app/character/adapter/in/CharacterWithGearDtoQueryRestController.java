package pl.app.character.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.character.query.CharacterQueryService;
import pl.app.character.query.CharacterWithGearDtoQueryService;
import pl.app.character.query.dto.CharacterDto;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.god_equipment.dto.GodEquipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(CharacterWithGearDtoQueryRestController.resourcePath)
@RequiredArgsConstructor
class CharacterWithGearDtoQueryRestController {
    public static final String resourceName = "character-with-gears";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final CharacterWithGearDtoQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<CharacterWithGearDto>>> fetchAllByIds(Pageable pageable, @RequestParam(required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<CharacterWithGearDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
