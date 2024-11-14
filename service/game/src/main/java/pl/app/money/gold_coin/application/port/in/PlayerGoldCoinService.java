package pl.app.money.gold_coin.application.port.in;

import pl.app.money.gold_coin.application.domain.PlayerGoldCoin;
import reactor.core.publisher.Mono;

public interface PlayerGoldCoinService {
    Mono<PlayerGoldCoin> crate(PlayerGoldCoinCommand.CreatePlayerGoldCoinCommand command);

    Mono<PlayerGoldCoin> mint(PlayerGoldCoinCommand.MintGoldCoinCommand command);
}
