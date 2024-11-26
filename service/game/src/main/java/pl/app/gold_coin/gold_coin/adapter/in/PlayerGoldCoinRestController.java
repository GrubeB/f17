package pl.app.gold_coin.gold_coin.adapter.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.app.building.builder.application.port.in.BuilderCommand;
import pl.app.building.builder.query.dto.BuilderDto;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinCommand;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinService;
import pl.app.player.player.service.PlayerQueryService;
import pl.app.player.player.service.PlayerService;
import pl.app.player.player.service.dto.PlayerCreateDto;
import pl.app.player.player.service.dto.PlayerDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(PlayerGoldCoinRestController.resourcePath)
@RequiredArgsConstructor
class PlayerGoldCoinRestController {
    public static final String resourceName = "gold-coins";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final PlayerGoldCoinService service;


    @PostMapping("/mint")
    Mono<ResponseEntity<Void>> mint(@RequestBody PlayerGoldCoinCommand.MintGoldCoinCommand command) {
        return service.mint(command)
                .thenReturn(ResponseEntity.accepted().build());
    }

}
