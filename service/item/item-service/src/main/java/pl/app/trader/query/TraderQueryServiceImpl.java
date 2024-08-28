package pl.app.trader.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.common.shared.model.ItemType;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;
import pl.app.trader.application.domain.Trader;
import pl.app.trader.dto.TraderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
class TraderQueryServiceImpl implements TraderQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public TraderQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<TraderDto> fetchByGodId(@NonNull ObjectId godId) {
        return repository.findByGodId(godId)
                .map(e -> mapper.map(e, TraderDto.class));
    }

    @Override
    public Mono<Page<TraderDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, TraderDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }


    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;
        @PostConstruct
        void init() {
            addMapper(Trader.class, TraderDto.class, this::mapToTraderDto);
        }

        TraderDto mapToTraderDto(Trader domain) {
            return new TraderDto(
                    domain.getGodId(),
                    domain.getItems().stream()
                            .filter(i -> ItemType.isOutfit(i.getType()))
                            .map(i -> modelMapper.map(i, OutfitDto.class))
                            .collect(Collectors.toSet()),
                    domain.getItems().stream()
                            .filter(i -> ItemType.isWeapon(i.getType()))
                            .map(i -> modelMapper.map(i, WeaponDto.class))
                            .collect(Collectors.toSet())
            );
        }

    }

    interface Repository extends ReactiveMongoRepository<Trader, ObjectId> {
        @Query("{ 'godId': ?0 }")
        Mono<Trader> findByGodId(ObjectId id);

        Flux<Trader> findAllBy(Pageable pageable);
    }
}
