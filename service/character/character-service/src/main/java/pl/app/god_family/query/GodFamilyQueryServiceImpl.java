package pl.app.god_family.query;

import god_family.application.domain.GodFamily;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
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
import pl.app.god_family.query.dto.GodFamilyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
class GodFamilyQueryServiceImpl implements GodFamilyQueryService {
    private final ReactiveMongoTemplate mongoTemplate;
    private final Mapper mapper;
    private final Repository repository;


    public GodFamilyQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<Page<GodFamilyDto>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, GodFamilyDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<GodFamilyDto> fetchByGodId(@NotNull ObjectId godId) {
        return repository.findByGodId(godId)
                .map(e -> mapper.map(e, GodFamilyDto.class));
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
        Flux<GodFamily> findAllBy(Pageable pageable);
        @Query("{ 'godId': ?0 }")
        Mono<GodFamily> findByGodId(ObjectId id);
    }
}
