package pl.app.tribe.query;

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
import pl.app.tribe.application.domain.Tribe;
import pl.app.tribe.query.dto.TribeDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class TribeDtoQueryServiceImpl implements TribeDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public TribeDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<TribeDto> fetchByVillageId(@NonNull ObjectId villageId) {
        return repository.findById(villageId)
                .map(e -> mapper.map(e, TribeDto.class));
    }

    @Override
    public Flux<TribeDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, TribeDto.class));
    }

    interface Repository extends ReactiveMongoRepository<Tribe, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Tribe.class, TribeDto.class, e -> modelMapper.map(e, TribeDto.class));
        }

    }
}
