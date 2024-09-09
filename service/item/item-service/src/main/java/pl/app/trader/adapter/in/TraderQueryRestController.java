package pl.app.trader.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.equipment.application.domain.EquipmentException;
import pl.app.trader.dto.TraderDto;
import pl.app.trader.query.TraderQueryService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(TraderQueryRestController.resourcePath)
@RequiredArgsConstructor
class TraderQueryRestController {
    public static final String resourceName = "traders";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final TraderQueryService queryService;

    @GetMapping("/{godId}")
    Mono<ResponseEntity<TraderDto>> fetchByGodId(
            @PathVariable ObjectId godId
    ) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(EquipmentException.NotFoundGodEquipmentException.fromGodId(godId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Page<TraderDto>>> fetchAllByPageable(
            Pageable pageable
    ) {
        return queryService.fetchAllByPageable(pageable)
                .map(ResponseEntity::ok);
    }

}
