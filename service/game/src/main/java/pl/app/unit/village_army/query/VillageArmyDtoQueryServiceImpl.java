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
import pl.app.unit.unit.application.domain.UnitType;
import pl.app.unit.village_army.application.domain.VillageArmy;
import pl.app.unit.village_army.query.dto.VillageArmyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        }

        VillageArmyDto mapToVillageArmyDto(VillageArmy domain) {
            return VillageArmyDto.builder()
                    .villageId(domain.getVillageId())
                    .spearmanNumber(domain.getArmy().get(UnitType.SPEARMAN))
                    .swordsmanNumber(domain.getArmy().get(UnitType.SWORDSMAN))
                    .archerNumber(domain.getArmy().get(UnitType.ARCHER))
                    .heavyCavalryNumber(domain.getArmy().get(UnitType.HEAVY_CAVALRY))
                    .axeFighterNumber(domain.getArmy().get(UnitType.AXE_FIGHTER))
                    .lightCavalryNumber(domain.getArmy().get(UnitType.LIGHT_CAVALRY))
                    .mountedArcherNumber(domain.getArmy().get(UnitType.MOUNTED_ARCHER))
                    .ramNumber(domain.getArmy().get(UnitType.RAM))
                    .catapultNumber(domain.getArmy().get(UnitType.CATAPULT))
                    .paladinNumber(domain.getArmy().get(UnitType.PALADIN))
                    .noblemanNumber(domain.getArmy().get(UnitType.NOBLEMAN))
                    .berserkerNumber(domain.getArmy().get(UnitType.BERSERKER))
                    .trebuchetNumber(domain.getArmy().get(UnitType.TREBUCHET))
                    .build();
        }
    }
}
