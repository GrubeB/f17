package pl.app.monster.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.common.shared.model.Progress;
import pl.app.common.shared.model.Statistics;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.dto.GearDto;
import pl.app.item.http.GearDtoQueryControllerHttpInterface;
import pl.app.item.http.LootDtoQueryControllerHttpInterface;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.dto.LootDto;
import pl.app.monster.application.domain.Monster;
import pl.app.monster.query.dto.MonsterWithGearDto;
import pl.app.monster_template.dto.ProgressDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class MonsterWithGearQueryServiceImpl implements MonsterWithGearDtoQueryService {
    private final Repository repository;
    private final GearDtoQueryControllerHttpInterface gearDtoQueryController;
    private final LootDtoQueryControllerHttpInterface lootDtoQueryController;
    private final Mapper mapper;


    public MonsterWithGearQueryServiceImpl(Mapper mapper,
                                           GearDtoQueryControllerHttpInterface gearDtoQueryController,
                                           LootDtoQueryControllerHttpInterface lootDtoQueryController,
                                           ReactiveMongoTemplate mongoTemplate) {
        this.lootDtoQueryController = lootDtoQueryController;
        this.gearDtoQueryController = gearDtoQueryController;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }


    @Override
    public Mono<MonsterWithGearDto> fetchById(@NotNull ObjectId id) {
        return Mono.zip(
                repository.findById(id),
                gearDtoQueryController.fetchByDomainObject(id, Gear.LootDomainObjectType.MONSTER).map(HttpEntity::getBody),
                lootDtoQueryController.fetchByDomainObject(id, Loot.LootDomainObjectType.MONSTER).map(HttpEntity::getBody)
        ).map(t -> mapper.mapToMonsterWithGearDto(t.getT1(), t.getT2(), t.getT3()));
    }

    @Override
    public Mono<Page<MonsterWithGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collect(Collectors.toSet())
                .flatMap(set -> this.fetchAllByIds(set.stream().map(Monster::getId).collect(Collectors.toList()), pageable));
    }

    @Override
    public Mono<Page<MonsterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchAllByPageable(pageable);
        }
        return Mono.zip(
                        repository.findAllById(ids).collect(Collectors.toSet()),
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
                        return mapper.mapToMonsterWithGearDto(monster, gear.get(), loot.get());
                    }
                    return mapper.mapToMonsterWithGearDto(monster);
                }).collect(Collectors.toList()))
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));

    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Monster.class, MonsterWithGearDto.class, this::mapToMonsterWithGearDto);
            addMapper(Monster.class, GearDto.class, LootDto.class, MonsterWithGearDto.class, this::mapToMonsterWithGearDto);
            addMapper(Progress.class, ProgressDto.class, e -> modelMapper.map(e, ProgressDto.class));
        }

        MonsterWithGearDto mapToMonsterWithGearDto(Monster monster, GearDto gear, LootDto loot) {
            Statistics baseStatistic = monster.getStatistics();
            Statistics gearStatistic = gear.getStatistic();
            Statistics sumStatistics = new Statistics().add(baseStatistic).add(gearStatistic);

            return new MonsterWithGearDto(
                    monster.getId(),
                    monster.getName(),
                    monster.getTemplate().getDescription(),
                    monster.getRace(),
                    monster.getProfession(),
                    monster.getImageId(),
                    monster.getLevel(),
                    baseStatistic, gearStatistic, sumStatistics,
                    Statistics.getHp(sumStatistics.getPersistence(), monster.getProfession()),
                    Statistics.getDef(sumStatistics.getDurability(), monster.getProfession()),
                    Statistics.getAttackPower(sumStatistics.getStrength(), monster.getProfession()),
                    gear,
                    loot,
                    map(monster.getProgress(), ProgressDto.class)
            );
        }

        MonsterWithGearDto mapToMonsterWithGearDto(Monster monster) {
            Statistics baseStatistic = monster.getStatistics();
            Statistics gearStatistic = Statistics.zero();
            Statistics sumStatistics = new Statistics().add(baseStatistic).add(gearStatistic);

            return new MonsterWithGearDto(
                    monster.getId(),
                    monster.getName(),
                    monster.getTemplate().getDescription(),
                    monster.getRace(),
                    monster.getProfession(),
                    monster.getImageId(),
                    monster.getLevel(),
                    baseStatistic, gearStatistic, sumStatistics,
                    Statistics.getHp(sumStatistics.getPersistence(), monster.getProfession()),
                    Statistics.getDef(sumStatistics.getDurability(), monster.getProfession()),
                    Statistics.getAttackPower(sumStatistics.getStrength(), monster.getProfession()),
                    new GearDto(),
                    new LootDto(),
                    map(monster.getProgress(), ProgressDto.class)
            );
        }

    }


    interface Repository extends ReactiveMongoRepository<Monster, ObjectId> {
        Flux<Monster> findAllBy(Pageable pageable);
    }
}
