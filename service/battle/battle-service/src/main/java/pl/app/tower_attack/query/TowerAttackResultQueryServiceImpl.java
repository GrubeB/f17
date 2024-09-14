package pl.app.tower_attack.query;

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
import pl.app.tower_attack.application.domain.TowerAttackResult;
import pl.app.tower_attack.query.dto.TowerAttackResultDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class TowerAttackResultQueryServiceImpl implements TowerAttackResultQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public TowerAttackResultQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<TowerAttackResultDto> fetchById(@NonNull ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, TowerAttackResultDto.class));
    }

    @Override
    public Mono<Page<TowerAttackResultDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, TowerAttackResultDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<TowerAttackResultDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllById(ids, pageable)
                .map(e -> mapper.map(e, TowerAttackResultDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<TowerAttackResultDto>> fetchAllByGodId(ObjectId godId, Pageable pageable) {
        return repository.findAllByGodId(godId, pageable)
                .map(e -> mapper.map(e, TowerAttackResultDto.class))
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
            addMapper(TowerAttackResult.class, TowerAttackResultDto.class, e -> modelMapper.map(e, TowerAttackResultDto.class));
        }
    }

    interface Repository extends ReactiveMongoRepository<TowerAttackResult, ObjectId> {

        Flux<TowerAttackResult> findAllBy(Pageable pageable);

        @Query("{ '_id.': { $in: ?0 } }")
        Flux<TowerAttackResult> findAllById(List<ObjectId> ids, Pageable pageable);

        @Query("{ 'godId.': ?0 }")
        Flux<TowerAttackResult> findAllByGodId(ObjectId godId, Pageable pageable);
    }
}
