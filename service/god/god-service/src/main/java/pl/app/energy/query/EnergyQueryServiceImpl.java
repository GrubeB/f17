package pl.app.energy.query;

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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.energy.application.domain.Energy;
import pl.app.energy.query.dto.EnergyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class EnergyQueryServiceImpl implements EnergyQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public EnergyQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<EnergyDto> fetchByGodId(ObjectId godId) {
        return repository.findByGodIdId(godId)
                .map(e -> mapper.map(e, EnergyDto.class));
    }

    @Override
    public Mono<List<EnergyDto>> fetchAllByGodIds(List<ObjectId> godIds) {
        return repository.findAllByGodIds(godIds)
                .map(e -> mapper.map(e, EnergyDto.class))
                .collectList();
    }

    @Override
    public Mono<Page<EnergyDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, EnergyDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<EnergyDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable) {
        if (Objects.isNull(godIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllByGodIds(godIds, pageable)
                .map(e -> mapper.map(e, EnergyDto.class))
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
            addMapper(Energy.class, EnergyDto.class, this::mapToEnergyDto);
        }
        EnergyDto mapToEnergyDto(Energy domain){
            return new EnergyDto(
                    domain.getGodId(),
                    domain.getCurrentEnergy()
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<Energy, ObjectId> {
        @Query("{ 'godId': ?0 }")
        Mono<Energy> findByGodIdId(ObjectId id);

        Flux<Energy> findAllBy(Pageable pageable);

        @Query("{ 'godId': { $in: ?0 } }")
        Flux<Energy> findAllByGodIds(List<ObjectId> ids, Pageable pageable);

        @Query("{ 'godId': { $in: ?0 } }")
        Flux<Energy> findAllByGodIds(List<ObjectId> ids);
    }
}
