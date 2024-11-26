package pl.app.player.player.service;

import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.player.player.service.dto.PlayerCreateDto;
import pl.app.player.player.service.dto.PlayerDto;
import pl.app.player.player.service.dto.PlayerUpdateDto;
import reactor.core.publisher.Mono;

public interface PlayerService {
    Mono<PlayerDto> create(@Valid PlayerCreateDto dto);
    Mono<PlayerDto> update(@NonNull ObjectId id, PlayerUpdateDto dto);
    Mono<Void> deleteById(@NonNull ObjectId id);
}
