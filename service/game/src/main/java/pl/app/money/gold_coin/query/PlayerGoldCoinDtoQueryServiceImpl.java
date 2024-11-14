package pl.app.money.gold_coin.query;

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
import pl.app.money.gold_coin.application.domain.PlayerGoldCoin;
import pl.app.money.gold_coin.query.dto.PlayerGoldCoinDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class PlayerGoldCoinDtoQueryServiceImpl implements PlayerGoldCoinDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public PlayerGoldCoinDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<PlayerGoldCoinDto> fetchByPlayerId(@NonNull ObjectId playerId) {
        return repository.findById(playerId)
                .map(e -> mapper.map(e, PlayerGoldCoinDto.class));
    }

    @Override
    public Flux<PlayerGoldCoinDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, PlayerGoldCoinDto.class));
    }

    interface Repository extends ReactiveMongoRepository<PlayerGoldCoin, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(PlayerGoldCoin.class, PlayerGoldCoinDto.class, this::mapToPlayerGoldCoinDto);
        }

        PlayerGoldCoinDto mapToPlayerGoldCoinDto(PlayerGoldCoin domain) {
            return new PlayerGoldCoinDto(
                    domain.getPlayerId(),
                    domain.getAmount(),
                    domain.getMaxNumberOfNobleMans()
            );
        }

    }
}
