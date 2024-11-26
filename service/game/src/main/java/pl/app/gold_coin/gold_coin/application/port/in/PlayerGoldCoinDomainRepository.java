package pl.app.gold_coin.gold_coin.application.port.in;

import org.bson.types.ObjectId;
import pl.app.gold_coin.gold_coin.application.domain.PlayerGoldCoin;
import reactor.core.publisher.Mono;

public interface PlayerGoldCoinDomainRepository {
    Mono<PlayerGoldCoin> fetchByPlayerId(ObjectId playerId);
}
