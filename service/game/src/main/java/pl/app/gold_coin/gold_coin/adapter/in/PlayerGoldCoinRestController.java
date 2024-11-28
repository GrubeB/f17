package pl.app.gold_coin.gold_coin.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinCommand;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinService;
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
