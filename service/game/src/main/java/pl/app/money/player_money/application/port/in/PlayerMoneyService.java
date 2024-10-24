package pl.app.money.player_money.application.port.in;

import pl.app.money.player_money.application.domain.PlayerMoney;
import pl.app.resource.village_resource.application.domain.VillageResource;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import reactor.core.publisher.Mono;

public interface PlayerMoneyService {
    Mono<PlayerMoney> crate(PlayerMoneyCommand.CreatePlayerMoneyCommand command);
    Mono<PlayerMoney> add(PlayerMoneyCommand.AddMoneyCommand command);
    Mono<PlayerMoney> subtract(PlayerMoneyCommand.SubtractMoneyCommand command);

}
