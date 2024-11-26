package pl.app.village.village.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.building.village_infrastructure.query.dto.VillageInfrastructureDto;
import pl.app.common.mapper.BaseMapper;
import pl.app.map.village_position.query.dto.VillagePositionDto;
import pl.app.player.player.service.dto.PlayerDto;
import pl.app.resource.village_resource.query.dto.VillageResourceDto;
import pl.app.unit.village_army.query.dto.VillageArmyDto;
import pl.app.village.village.query.dto.VillageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class VillageDtoQueryServiceImpl implements VillageDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public VillageDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<VillageDto> fetchById(ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, VillageDto.class));
    }

    @Override
    public Flux<VillageDto> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, VillageDto.class));
    }

    @Override
    public Flux<VillageDto> fetchByPlayerId(ObjectId playerId) {
        return repository.findByOwnerId(playerId)
                .map(e -> mapper.map(e, VillageDto.class));
    }

    interface Repository extends ReactiveMongoRepository<VillageQuery, ObjectId> {
        @Query("{ 'ownerId' : ?0 }")
        Flux<VillageQuery> findByOwnerId(ObjectId ownerId);
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(VillageQuery.class, VillageDto.class, this::mapToVillageDto);
        }

        VillageDto mapToVillageDto(VillageQuery domain) {
            return VillageDto.builder()
                    .id(domain.getId())
                    .type(domain.getType())
                    .owner(map(domain.getPlayer(), PlayerDto.class))
                    .loyalty(domain.getVillageLoyalty().getLoyalty())
                    .villagePosition(map(domain.getVillagePosition(), VillagePositionDto.class))
                    .villageResource(map(domain.getVillageResource(), VillageResourceDto.class))
                    .villageInfrastructure(map(domain.getVillageInfrastructure(), VillageInfrastructureDto.class))
                    .villageArmy(map(domain.getVillageArmy(), VillageArmyDto.class))
                    .build();
        }
    }
}
