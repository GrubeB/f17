package pl.app.unit.village_army.query;

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
import pl.app.unit.village_army.application.domain.VillageArmy;
import pl.app.unit.village_army.query.dto.VillageArmyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
class VillageArmyDtoQueryServiceImpl implements VillageArmyDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public VillageArmyDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<VillageArmyDto> fetchByVillageId(@NonNull ObjectId villageId) {
        return repository.findById(villageId)
                .map(e -> mapper.map(e, VillageArmyDto.class));
    }

    @Override
    public Flux<VillageArmyDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, VillageArmyDto.class));
    }

    interface Repository extends ReactiveMongoRepository<VillageArmy, ObjectId> {
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(VillageArmy.class, VillageArmyDto.class, this::mapToVillageArmyDto);
            addMapper(VillageArmy.VillageSupport.class, VillageArmyDto.VillageSupportDto.class, e -> modelMapper.map(e, VillageArmyDto.VillageSupportDto.class));
        }

        VillageArmyDto mapToVillageArmyDto(VillageArmy domain) {
            return VillageArmyDto.builder()
                    .villageId(domain.getVillageId())
                    .villageArmy(domain.getVillageArmy())
                    .supportArmy(domain.getSupportArmy())
                    .blockedArmy(domain.getBlockedArmy())
                    .villageSupports(domain.getVillageSupports().stream().map(e -> map(e, VillageArmyDto.VillageSupportDto.class)).collect(Collectors.toList()))
                    .build();
        }
    }
}
