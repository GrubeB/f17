package pl.app.army_walk.army_walk.query;

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
import pl.app.army_walk.army_walk.domain.application.ArmyWalk;
import pl.app.army_walk.army_walk.query.dto.ArmyWalkDto;
import pl.app.common.mapper.BaseMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class ArmyWalkDtoQueryServiceImpl implements ArmyWalkDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public ArmyWalkDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<ArmyWalkDto> fetchById(@NonNull ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, ArmyWalkDto.class));
    }


    @Override
    public Flux<ArmyWalkDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, ArmyWalkDto.class));
    }


    interface Repository extends ReactiveMongoRepository<ArmyWalk, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(ArmyWalk.class, ArmyWalkDto.class, e -> modelMapper.map(e, ArmyWalkDto.class));
        }

    }
}
