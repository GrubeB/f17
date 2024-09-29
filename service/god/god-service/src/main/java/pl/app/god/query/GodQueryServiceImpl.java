package pl.app.god.query;

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
import pl.app.energy.application.domain.Energy;
import pl.app.god.application.domain.God;
import pl.app.god.query.dto.GodDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
class GodQueryServiceImpl implements GodQueryService {
    private final Mapper mapper;
    private final Repository repository;


    public GodQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<GodDto> fetchById(@NonNull ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, GodDto.class));
    }

    @Override
    public Mono<List<GodDto>> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, GodDto.class))
                .collectList();
    }

    @Override
    public Mono<List<GodDto>> fetchAllByIds(List<ObjectId> godIds) {
        return repository.findAllByGodIds(godIds)
                .map(e -> mapper.map(e, GodDto.class))
                .collectList();
    }

    @Override
    public Mono<Page<GodDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, GodDto.class))
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
            addMapper(God.class, GodDto.class, this::mapToGodDto);

        }

        GodDto mapToGodDto(God domain) {
            return new GodDto(
                    domain.getId(),
                    domain.getName(),
                    domain.getDescription(),
                    domain.getImageId(),
                    domain.getMoney()
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<God, ObjectId> {
        Flux<God> findAllBy(Pageable pageable);
        @Query("{ 'godId': { $in: ?0 } }")
        Flux<God> findAllByGodIds(List<ObjectId> ids);
    }
}
