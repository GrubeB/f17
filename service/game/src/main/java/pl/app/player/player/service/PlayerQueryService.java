package pl.app.player.player.service;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.player.player.service.dto.PlayerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerQueryService {
    Flux<PlayerDto> fetchAll();
    Mono<PlayerDto> fetchById(@NonNull ObjectId id);
}
