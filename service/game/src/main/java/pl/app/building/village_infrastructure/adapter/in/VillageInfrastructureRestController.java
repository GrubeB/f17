package pl.app.building.village_infrastructure.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.building.village_infrastructure.query.VillageInfrastructureDtoQueryService;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import pl.app.village.village.query.VillageDtoQueryService;
import pl.app.village.village.query.dto.VillageDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VillageInfrastructureRestController.resourcePath)
@RequiredArgsConstructor
class VillageInfrastructureRestController {
    public static final String resourceName = "village-infrastructures";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final VillageInfrastructureService service;
    private final VillageInfrastructureDtoQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<VillageInfrastructureDto>> crate(@RequestBody VillageInfrastructureCommand.CreateVillageInfrastructureCommand command) {
        return service.crate(command)
                .flatMap(domain -> queryService.fetchByVillageId(domain.getVillageId()))
                .map(ResponseEntity::ok);
    }
}
