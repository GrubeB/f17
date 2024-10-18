package pl.app.building.village_infrastructure.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.building.village_infrastructure.application.domain.VillageInfrastructure;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import pl.app.common.mapper.BaseMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class VillageInfrastructureDtoQueryServiceImpl implements VillageInfrastructureDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public VillageInfrastructureDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<VillageInfrastructureDto> fetchByVillageId(ObjectId villageId) {
        return repository.findById(villageId)
                .map(e -> mapper.map(e, VillageInfrastructureDto.class));
    }

    @Override
    public Flux<VillageInfrastructureDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, VillageInfrastructureDto.class));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(VillageInfrastructure.class, VillageInfrastructureDto.class, e -> modelMapper.map(e, VillageInfrastructureDto.class));
        }
    }

    interface Repository extends ReactiveMongoRepository<VillageInfrastructure, ObjectId> {
    }
}
