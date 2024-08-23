package pl.app.god_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{id}")
    Mono<ResponseEntity<CharacterGearDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    Mono<ResponseEntity<Page<CharacterGearDto>>> fetchAllByIds(Pageable pageable,  @RequestParam List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }


}
