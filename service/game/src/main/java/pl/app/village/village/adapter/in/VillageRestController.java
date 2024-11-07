package pl.app.village.village.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import pl.app.village.village.query.VillageDtoQueryService;
import pl.app.village.village.query.dto.VillageDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VillageRestController.resourcePath)
@RequiredArgsConstructor
class VillageRestController {
    public static final String resourceName = "villages";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final VillageService service;
    private final VillageDtoQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<VillageDto>> crate(@RequestBody VillageCommand.CreatePlayerVillageCommand command) {
        return service.cratePlayerVillage(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
