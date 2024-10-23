package pl.app.attack.army_walk.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.app.attack.army_walk.domain.port.in.ArmyWalkCommand;
import pl.app.attack.army_walk.domain.port.in.ArmyWalkService;
import pl.app.attack.army_walk.query.ArmyWalkDtoQueryService;
import pl.app.attack.army_walk.query.dto.ArmyWalkDto;
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
        return service.sendArmy(command)
                .flatMap(domain -> queryService.fetchById(domain.getArmyWalkId()))
                .map(ResponseEntity::ok);
    }
}
