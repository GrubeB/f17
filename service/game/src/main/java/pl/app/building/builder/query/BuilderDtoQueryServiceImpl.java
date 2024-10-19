package pl.app.building.builder.query;

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
import pl.app.building.builder.application.domain.Builder;
import pl.app.building.builder.query.dto.BuilderDto;
import pl.app.common.mapper.BaseMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class BuilderDtoQueryServiceImpl implements BuilderDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public BuilderDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<BuilderDto> fetchByVillageId(@NonNull ObjectId villageId) {
        return repository.findById(villageId)
                .map(e -> mapper.map(e, BuilderDto.class));
    }


    @Override
    public Flux<BuilderDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, BuilderDto.class));
    }


    interface Repository extends ReactiveMongoRepository<Builder, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Builder.class, BuilderDto.class, e -> modelMapper.map(e, BuilderDto.class));
        }
    }
}
