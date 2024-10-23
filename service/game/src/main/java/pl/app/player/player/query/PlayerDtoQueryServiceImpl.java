package pl.app.player.player.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.player.player.application.domain.Player;
import pl.app.player.player.query.dto.PlayerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class PlayerDtoQueryServiceImpl implements PlayerDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public PlayerDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

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

    interface Repository extends ReactiveMongoRepository<Player, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Player.class, PlayerDto.class, e -> modelMapper.map(e, PlayerDto.class));
        }
    }
}
