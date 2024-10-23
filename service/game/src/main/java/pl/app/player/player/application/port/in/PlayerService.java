package pl.app.player.player.application.port.in;

import pl.app.player.player.application.domain.Player;
import pl.app.unit.village_army.application.domain.VillageArmy;
import reactor.core.publisher.Mono;

public interface PlayerService {
    Mono<Player> crate(PlayerCommand.CreatePlayerCommand command);
}
