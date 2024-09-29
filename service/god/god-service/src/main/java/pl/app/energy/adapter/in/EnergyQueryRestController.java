package pl.app.energy.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.energy.application.domain.EnergyException;
import pl.app.energy.query.EnergyQueryService;
import pl.app.energy.query.dto.EnergyDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(EnergyQueryRestController.resourcePath)
@RequiredArgsConstructor
class EnergyQueryRestController {
    public static final String resourceName = "energies";
    public static final String resourcePath = "/api/v1/gods/{godId}/" + resourceName;

    private final EnergyQueryService queryService;

    @GetMapping
    Mono<ResponseEntity<EnergyDto>> fetchByGodId(@PathVariable ObjectId godId) {
        return queryService.fetchByGodId(godId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(EnergyException.NotFoundEnergyException.fromGodId(godId.toHexString())));
    }
}
