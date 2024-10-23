package pl.app.player.player.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.player.player.query.dto.PlayerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerDtoQueryService {
    Mono<PlayerDto> fetchById(@NonNull ObjectId playerId);

    Flux<PlayerDto> fetchAll();
}
