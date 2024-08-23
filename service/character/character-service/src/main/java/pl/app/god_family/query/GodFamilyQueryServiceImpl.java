package pl.app.god_family.query;

import god_family.application.domain.GodFamily;
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
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class GodFamilyQueryServiceImpl implements GodFamilyQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public GodFamilyQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<GodFamilyDto> fetchByGodId(@NonNull ObjectId godId) {
        return repository.findByGodId(godId)
                .map(e -> mapper.map(e, GodFamilyDto.class));
    }

    @Override
    public Mono<Page<GodFamilyDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, GodFamilyDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<GodFamilyDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable) {
        if (Objects.isNull(godIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllByGodId(godIds, pageable)
                .map(e -> mapper.map(e, GodFamilyDto.class))
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
            addMapper(GodFamily.class, GodFamilyDto.class, e -> modelMapper.map(e, GodFamilyDto.class));
        }
    }

    interface Repository extends ReactiveMongoRepository<GodFamily, ObjectId> {
        @Query("{ 'godId': ?0 }")
        Mono<GodFamily> findByGodId(ObjectId id);

        Flux<GodFamily> findAllBy(Pageable pageable);

        @Query("{ 'godId.': { $in: ?0 } }")
        Flux<GodFamily> findAllByGodId(List<ObjectId> ids, Pageable pageable);
    }
}
