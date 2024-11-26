package pl.app.gold_coin.gold_coin.application.port.in;

import pl.app.gold_coin.gold_coin.application.domain.PlayerGoldCoin;
import reactor.core.publisher.Mono;

public interface PlayerGoldCoinService {
    Mono<PlayerGoldCoin> crate(PlayerGoldCoinCommand.CreatePlayerGoldCoinCommand command);

    Mono<PlayerGoldCoin> mint(PlayerGoldCoinCommand.MintGoldCoinCommand command);
}
