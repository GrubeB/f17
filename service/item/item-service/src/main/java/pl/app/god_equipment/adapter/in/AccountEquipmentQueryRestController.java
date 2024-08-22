package pl.app.god_equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.god_equipment.dto.GodEquipmentDto;
import pl.app.god_equipment.query.GodEquipmentQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AccountEquipmentQueryRestController.resourcePath)
@RequiredArgsConstructor
class AccountEquipmentQueryRestController {
    public static final String resourceName = "god-equipments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodEquipmentQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<Page<GodEquipmentDto>>> fetchAllByPageable(Pageable pageable) {
        return queryService.fetchByPageable(pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<GodEquipmentDto>> fetchById(@PathVariable ObjectId id) {
        return queryService.fetchById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
