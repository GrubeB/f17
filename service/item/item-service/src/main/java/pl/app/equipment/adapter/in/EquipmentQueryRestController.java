package pl.app.equipment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.equipment.application.domain.EquipmentException;
import pl.app.equipment.dto.EquipmentDto;
import pl.app.equipment.query.EquipmentQueryService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(EquipmentQueryRestController.resourcePath)
@RequiredArgsConstructor
class EquipmentQueryRestController {
    public static final String resourceName = "equipments";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final EquipmentQueryService queryService;

    @GetMapping("/{godId}")
    Mono<ResponseEntity<EquipmentDto>> fetchByGodId(
            @PathVariable ObjectId godId
    ) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(EquipmentException.NotFoundGodEquipmentException.fromGodId(godId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<EquipmentDto>>> fetchAllByGodIds(
            Pageable pageable,
            @RequestParam(required = false) List<ObjectId> ids
    ) {
        return queryService.fetchAllByGodIds(ids, pageable)
                .map(ResponseEntity::ok);
    }

}
