package pl.app.tower.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.dto.GearDto;
import pl.app.item.http.GearDtoQueryControllerHttpInterface;
import pl.app.item.http.LootDtoQueryControllerHttpInterface;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.dto.LootDto;
import pl.app.monster.application.domain.Monster;
import pl.app.monster.query.MonsterWithGearDtoQueryService;
import pl.app.monster.query.dto.MonsterWithGearDto;
import pl.app.tower.application.domain.TowerLevel;
import pl.app.tower.dto.TowerLevelDto;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
class TowerLevelQueryServiceImpl implements TowerLevelQueryService {
    private final Mapper mapper;
    private final Repository repository;
    private final MonsterWithGearDtoQueryService monsterWithGearDtoQueryService;


    public TowerLevelQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper, MonsterWithGearDtoQueryService monsterWithGearDtoQueryService) {
        this.mapper = mapper;
        this.monsterWithGearDtoQueryService = monsterWithGearDtoQueryService;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<TowerLevelDto> fetchByLevel(@NonNull Integer id) {
        return repository.findByLevel(id)
                .map(e -> mapper.map(e, TowerLevelDto.class));
    }

    @Override
    public Mono<Page<TowerLevelDto>> fetchAll() {
        return repository.findAll()
                .map(e -> mapper.map(e, TowerLevelDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), PageRequest.of(0, Integer.MAX_VALUE), tuple.getT2()));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;
        private final GearDtoQueryControllerHttpInterface gearDtoQueryController;
        private final LootDtoQueryControllerHttpInterface lootDtoQueryController;

        @PostConstruct
        void init() {
            addMapper(TowerLevel.class, TowerLevelDto.class, this::mapToTowerLevelDto);
        }

        private TowerLevelDto mapToTowerLevelDto(TowerLevel domain) {
            var ids = domain.getMonsters().stream().map(Monster::getId).toList();
            return Mono.zip(
                            Mono.just(domain.getMonsters()),
                            gearDtoQueryController
                                    .fetchByDomainObject(Gear.LootDomainObjectType.MONSTER, ids)
                                    .map(HttpEntity::getBody)
                                    .map(Streamable::get)
                                    .map(s -> s.collect(Collectors.toSet())),
                            lootDtoQueryController.fetchByDomainObject(Loot.LootDomainObjectType.MONSTER, ids)
                                    .map(HttpEntity::getBody)
                                    .map(Streamable::get)
                                    .map(s -> s.collect(Collectors.toSet()))
                    ).map(t -> t.getT1().stream().map(monster -> {
                        Optional<GearDto> gear = t.getT2().stream().filter(e -> e.getId().equals(monster.getId())).findAny();
                        Optional<LootDto> loot = t.getT3().stream().filter(e -> e.getId().equals(monster.getId())).findAny();
                        if (gear.isPresent() && loot.isPresent()) {
                            return map(monster, gear.get(), loot.get(), MonsterWithGearDto.class);
                        }
                        return map(monster, MonsterWithGearDto.class);
                    }).collect(Collectors.toSet()))
                    .map(monsters -> new TowerLevelDto(
                            domain.getLevel(),
                            monsters,
                            domain.getMinNumberOfMonstersInBattle(),
                            domain.getMaxNumberOfMonstersInBattle()
                    )).block();
        }
    }

    interface Repository extends ReactiveMongoRepository<TowerLevel, ObjectId> {
        @Query("{ 'level': ?0 }")
        Mono<TowerLevel> findByLevel(Integer id);
    }
}
