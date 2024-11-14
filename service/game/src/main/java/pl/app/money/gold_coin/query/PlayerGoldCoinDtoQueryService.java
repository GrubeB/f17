package pl.app.money.gold_coin.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.money.gold_coin.query.dto.PlayerGoldCoinDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerGoldCoinDtoQueryService {
    Mono<PlayerGoldCoinDto> fetchByPlayerId(@NonNull ObjectId playerId);

    Flux<PlayerGoldCoinDto> fetchAll();
}
