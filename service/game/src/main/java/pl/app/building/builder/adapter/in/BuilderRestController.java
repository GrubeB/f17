package pl.app.building.builder.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.application.port.in.BuilderService;
import pl.app.building.builder.query.BuilderDtoQueryService;
import pl.app.building.builder.query.dto.BuilderDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(BuilderRestController.resourcePath)
@RequiredArgsConstructor
class BuilderRestController {
    public static final String resourceName = "builders";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final BuilderService service;
    private final BuilderDtoQueryService queryService;


    @PostMapping("/{villageId}/constructs")
    Mono<ResponseEntity<BuilderDto>> add(@RequestBody BuilderCommand.AddBuildingToConstructCommand command) {
        return service.add(command)
                .flatMap(domain -> queryService.fetchByVillageId(domain.getVillageId()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{villageId}/constructs")
    Mono<ResponseEntity<BuilderDto>> cancel(@RequestBody BuilderCommand.CancelBuildingConstructCommand command) {
        return service.cancel(command)
                .flatMap(domain -> queryService.fetchByVillageId(domain.getVillageId()))
                .map(ResponseEntity::ok);
    }
}
