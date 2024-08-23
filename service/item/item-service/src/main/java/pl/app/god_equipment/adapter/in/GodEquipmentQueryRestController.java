package pl.app.god_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.god_equipment.dto.GodEquipmentDto;
import pl.app.god_equipment.query.GodEquipmentQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(GodEquipmentQueryRestController.resourcePath)
@RequiredArgsConstructor
class GodEquipmentQueryRestController {
    public static final String resourceName = "god-equipments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodEquipmentQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<GodEquipmentDto>>> fetchAllByIds(Pageable pageable, @RequestParam(required = false) List<ObjectId> ids) {
        return queryService.fetchAllByIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<GodEquipmentDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
