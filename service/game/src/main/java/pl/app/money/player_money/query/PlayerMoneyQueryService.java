package pl.app.money.player_money.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.money.player_money.query.dto.PlayerMoneyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerMoneyQueryService {
    Mono<PlayerMoneyDto> fetchByPlayerId(@NonNull ObjectId playerId);

    Flux<PlayerMoneyDto> fetchAll();
}
