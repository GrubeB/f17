package pl.app.monster_gear.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_equipment.application.domain.GodEquipmentException;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.monster_gear.application.domain.MonsterGear;
import pl.app.monster_gear.application.domain.MonsterGearException;
import pl.app.monster_gear.dto.MonsterGearDto;
import pl.app.monster_gear.query.MonsterGearDtoQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(MonsterGearDtoQueryRestController.resourcePath)
@RequiredArgsConstructor
class MonsterGearDtoQueryRestController {
    public static final String resourceName = "monster-gears";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final MonsterGearDtoQueryService queryService;

    @GetMapping("/{monsterId}")
    Mono<ResponseEntity<MonsterGearDto>> fetchByMonsterId(
            @PathVariable ObjectId monsterId) {
        return queryService.fetchByMonsterId(monsterId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(MonsterGearException.NotFoundMonsterGearException.fromId(monsterId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<MonsterGearDto>>> fetchAllByMonsterIds(
            Pageable pageable,
            @RequestParam List<ObjectId> ids) {
        return queryService.fetchAllByMonsterIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
