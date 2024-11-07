package pl.app.money.player_money.application.port.in;

import org.bson.types.ObjectId;
import pl.app.money.player_money.application.domain.PlayerMoney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerMoneyDomainRepository {
    Mono<PlayerMoney> fetchByPlayerId(ObjectId playerId);

    Flux<PlayerMoney> fetchAll();
}
