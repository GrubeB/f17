package pl.app.money.player_money.application.port.in;

import pl.app.money.player_money.application.domain.PlayerMoney;
import reactor.core.publisher.Mono;

public interface PlayerMoneyService {
    Mono<PlayerMoney> crate(PlayerMoneyCommand.CreatePlayerMoneyCommand command);

    Mono<PlayerMoney> add(PlayerMoneyCommand.AddMoneyCommand command);

    Mono<PlayerMoney> subtract(PlayerMoneyCommand.SubtractMoneyCommand command);

}
