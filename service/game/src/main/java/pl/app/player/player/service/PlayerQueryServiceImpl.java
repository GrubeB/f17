package pl.app.player.player.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import pl.app.player.player.service.dto.PlayerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class PlayerQueryServiceImpl implements PlayerQueryService {
    private final PlayerMapper mapper;
    private final PlayerRepository repository;

    @Override
    public Mono<PlayerDto> fetchById(@NonNull ObjectId playerId) {
        return repository.findById(playerId)
                .map(e -> mapper.map(e, PlayerDto.class));
    }

    @Override
    public Flux<PlayerDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, PlayerDto.class));
    }

}
