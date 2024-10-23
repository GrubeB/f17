package pl.app.player.player.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.player.player.application.domain.PlayerException;
import pl.app.player.player.query.PlayerDtoQueryService;
import pl.app.player.player.query.dto.PlayerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(PlayerQueryRestController.resourcePath)
@RequiredArgsConstructor
class PlayerQueryRestController {
    public static final String resourceName = "players";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final PlayerDtoQueryService queryService;

    @GetMapping("/{playerId}")
    Mono<ResponseEntity<PlayerDto>> fetchByVillageId(@PathVariable ObjectId playerId) {
        return queryService.fetchById(playerId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(PlayerException.NotFoundPlayerException.fromId(playerId.toHexString())));
    }

    @GetMapping
    Mono<ResponseEntity<Flux<PlayerDto>>> fetchAll() {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll()));
    }
}
