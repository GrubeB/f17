package pl.app.army_walk.army_walk.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.army_walk.army_walk.domain.port.in.ArmyWalkCommand;
import pl.app.army_walk.army_walk.domain.port.in.ArmyWalkService;
import pl.app.army_walk.army_walk.query.ArmyWalkDtoQueryService;
import pl.app.army_walk.army_walk.query.dto.ArmyWalkDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ArmyWalkRestController.resourcePath)
@RequiredArgsConstructor
class ArmyWalkRestController {
    public static final String resourceName = "army-walks";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ArmyWalkService service;
    private final ArmyWalkDtoQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<ArmyWalkDto>> sendArmy(@RequestBody ArmyWalkCommand.SendArmyCommand command) {
        return service.send(command)
                .flatMap(domain -> queryService.fetchById(domain.getArmyWalkId()))
                .map(ResponseEntity::ok);
    }
}
