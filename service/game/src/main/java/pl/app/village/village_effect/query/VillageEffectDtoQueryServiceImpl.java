package pl.app.village.village_effect.query;

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
import pl.app.village.village_effect.application.domain.VillageEffect;
import pl.app.village.village_effect.query.dto.VillageEffectDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class VillageEffectDtoQueryServiceImpl implements VillageEffectDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public VillageEffectDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<VillageEffectDto> fetchByVillageId(@NonNull ObjectId villageId) {
        return repository.findById(villageId)
                .map(e -> mapper.map(e, VillageEffectDto.class));
    }

    @Override
    public Flux<VillageEffectDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, VillageEffectDto.class));
    }

    interface Repository extends ReactiveMongoRepository<VillageEffect, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(VillageEffect.class, VillageEffectDto.class, e -> modelMapper.map(e, VillageEffectDto.class));
        }

    }
}
