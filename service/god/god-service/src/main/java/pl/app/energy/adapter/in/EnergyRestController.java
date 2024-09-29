package pl.app.energy.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.energy.application.port.in.EnergyCommand;
import pl.app.energy.application.port.in.EnergyService;
import pl.app.energy.query.EnergyQueryService;
import pl.app.energy.query.dto.EnergyDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(EnergyRestController.resourcePath)
@RequiredArgsConstructor
class EnergyRestController {
    public static final String resourceName = "energies";
    public static final String resourcePath = "/api/v1/gods/{godId}/" + resourceName;

    private final EnergyService service;
    private final EnergyQueryService queryService;

    @PostMapping("/add")
    Mono<ResponseEntity<EnergyDto>> addEnergy(@PathVariable ObjectId godId,
                                              @RequestBody EnergyCommand.AddEnergyCommand command) {
        return service.addEnergy(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subtract")
    Mono<ResponseEntity<EnergyDto>> subtractEnergy(@PathVariable ObjectId godId,
                                                   @RequestBody EnergyCommand.SubtractEnergyCommand command) {
        return service.subtractEnergy(command)
                .flatMap(domain -> queryService.fetchByGodId(domain.getGodId()))
                .map(ResponseEntity::ok);
    }
}
