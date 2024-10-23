package pl.app.player.player.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.player.player.application.port.in.PlayerCommand;
import pl.app.player.player.application.port.in.PlayerService;
import pl.app.player.player.query.PlayerDtoQueryService;
import pl.app.player.player.query.dto.PlayerDto;
import reactor.core.publisher.Mono;

// TODO to remove
@RestController
@RequestMapping(PlayerRestController.resourcePath)
@RequiredArgsConstructor
class PlayerRestController {
    public static final String resourceName = "players";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final PlayerService service;
    private final PlayerDtoQueryService queryService;


    @PostMapping
    Mono<ResponseEntity<PlayerDto>> crate(@RequestBody PlayerCommand.CreatePlayerCommand command) {
        return service.crate(command)
                .flatMap(domain -> queryService.fetchById(domain.getPlayerId()))
                .map(ResponseEntity::ok);
    }
}
