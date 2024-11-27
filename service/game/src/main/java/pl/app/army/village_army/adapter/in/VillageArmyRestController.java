package pl.app.army.village_army.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.army.village_army.application.port.in.VillageArmyCommand;
import pl.app.army.village_army.application.port.in.VillageArmyService;
import pl.app.army.village_army.query.VillageArmyDtoQueryService;
import pl.app.army.village_army.query.dto.VillageArmyDto;
import reactor.core.publisher.Mono;

// TODO to remove
@RestController
@RequestMapping(VillageArmyRestController.resourcePath)
@RequiredArgsConstructor
class VillageArmyRestController {
    public static final String resourceName = "village-armies";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final VillageArmyService service;
    private final VillageArmyDtoQueryService queryService;


    @PostMapping("/{villageId}/units")
    Mono<ResponseEntity<VillageArmyDto>> add(@RequestBody VillageArmyCommand.AddUnitsCommand command) {
        return service.add(command)
                .flatMap(domain -> queryService.fetchByVillageId(domain.getVillageId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{villageId}/constructs")
    Mono<ResponseEntity<VillageArmyDto>> subtract(@RequestBody VillageArmyCommand.SubtractUnitsCommand command) {
        return service.subtract(command)
                .flatMap(domain -> queryService.fetchByVillageId(domain.getVillageId()))
                .map(ResponseEntity::ok);
    }
}
