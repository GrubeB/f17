package pl.app.god.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.god.application.port.in.GodCommand;
import pl.app.god.application.port.in.GodService;
import pl.app.god.query.GodQueryService;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(GodRestController.resourcePath)
@RequiredArgsConstructor
class GodRestController {
    public static final String resourceName = "gods";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final GodService service;
    private final GodQueryService queryService;


    @PostMapping
    public Mono<ResponseEntity<GodDto>> create(@RequestBody GodCommand.CreateGodCommand command) {
        return service.create(command)
                .flatMap(domain -> queryService.fetchById(domain.getId()))
                .map(ResponseEntity::ok);
    }
}
