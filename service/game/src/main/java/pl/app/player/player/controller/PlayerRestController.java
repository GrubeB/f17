package pl.app.player.player.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.player.player.service.PlayerQueryService;
import pl.app.player.player.service.PlayerService;
import pl.app.player.player.service.dto.PlayerCreateDto;
import pl.app.player.player.service.dto.PlayerDto;
import reactor.core.publisher.Mono;

// TODO to remove
@RestController
@RequestMapping(PlayerRestController.resourcePath)
@RequiredArgsConstructor
class PlayerRestController {
    public static final String resourceName = "players";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final PlayerService service;
    private final PlayerQueryService queryService;

    @PostMapping
    Mono<ResponseEntity<PlayerDto>> crate() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .flatMap(jwt -> {
                    String accountId = jwt.getSubject();
                    String preferredUsername = jwt.getClaimAsString("preferred_username");
                    return service.create(new PlayerCreateDto(accountId, preferredUsername, ""))
                            .flatMap(domain -> queryService.fetchById(domain.getPlayerId()))
                            .map(ResponseEntity::ok);
                });
    }
}
