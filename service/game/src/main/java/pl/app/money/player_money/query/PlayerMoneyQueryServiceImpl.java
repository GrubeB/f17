package pl.app.money.player_money.query;

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
import pl.app.money.player_money.application.domain.PlayerMoney;
import pl.app.money.player_money.query.dto.PlayerMoneyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class PlayerMoneyQueryServiceImpl implements PlayerMoneyQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public PlayerMoneyQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<PlayerMoneyDto> fetchByPlayerId(@NonNull ObjectId playerId) {
        return repository.findById(playerId)
                .map(e -> mapper.map(e, PlayerMoneyDto.class));
    }

    @Override
    public Flux<PlayerMoneyDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, PlayerMoneyDto.class));
    }

    interface Repository extends ReactiveMongoRepository<PlayerMoney, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(PlayerMoney.class, PlayerMoneyDto.class, e -> modelMapper.map(e, PlayerMoneyDto.class));
        }

    }
}
