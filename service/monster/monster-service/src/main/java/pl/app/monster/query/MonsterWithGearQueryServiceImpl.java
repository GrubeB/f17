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
import pl.app.common.shared.model.Statistics;
import pl.app.item.http.MonsterGearDtoQueryControllerHttpInterface;
import pl.app.monster.application.domain.Monster;
import pl.app.monster.query.dto.MonsterWithGearDto;
import pl.app.monster_gear.dto.MonsterGearDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class MonsterWithGearQueryServiceImpl implements MonsterWithGearDtoQueryService {
    private final Repository repository;
    private final MonsterGearDtoQueryControllerHttpInterface monsterGearDtoQueryController;
    private final Mapper mapper;


    public MonsterWithGearQueryServiceImpl(Mapper mapper, MonsterGearDtoQueryControllerHttpInterface monsterGearDtoQueryController, ReactiveMongoTemplate mongoTemplate) {
        this.monsterGearDtoQueryController = monsterGearDtoQueryController;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }


    @Override
    public Mono<MonsterWithGearDto> fetchById(@NotNull ObjectId id) {
        return repository.findById(id)
                .zipWith(monsterGearDtoQueryController.fetchByMonsterId(id).map(HttpEntity::getBody))
                .map(t -> mapper.mapToMonsterWithGearDto(t.getT1(), t.getT2()));
    }

    @Override
    public Mono<Page<MonsterWithGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collect(Collectors.toSet())
                .map(set -> monsterGearDtoQueryController
                        .fetchAllByMonsterIds(set.stream().map(Monster::getId).collect(Collectors.toList()))
                        .map(HttpEntity::getBody)
                        .map(Streamable::get)
                        .map(s -> s.collect(Collectors.toSet()))
                        .map(g -> Tuples.of(set, g)))
                .flatMap(Function.identity())
                .map(t -> mapper.mapToMonsterWithGearDto(t.getT1(), t.getT2()))
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<MonsterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllById(ids)
                .collect(Collectors.toSet())
                .zipWith(monsterGearDtoQueryController
                        .fetchAllByMonsterIds(ids)
                        .map(HttpEntity::getBody)
                        .map(Streamable::get)
                        .map(s -> s.collect(Collectors.toSet()))

                ).map(t -> mapper.mapToMonsterWithGearDto(t.getT1(), t.getT2()))
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
        }

        public List<MonsterWithGearDto> mapToMonsterWithGearDto(Set<Monster> characters, Set<MonsterGearDto> gears) {
            return characters.stream().map(monster -> {
                Optional<MonsterGearDto> gear = gears.stream().filter(g -> g.getMonsterId().equals(monster.getId())).findAny();
                return gear.map(g -> mapToMonsterWithGearDto(monster, g)).orElse(mapToMonsterWithGearDto(monster));
            }).collect(Collectors.toList());
        }

        public MonsterWithGearDto mapToMonsterWithGearDto(Monster monster, MonsterGearDto gear) {
            Statistics baseStatistic = monster.getStatistics();
            Statistics gearStatistic = gear.getStatistic();
            Statistics sumStatistics = new Statistics().mergeWith(baseStatistic).mergeWith(gearStatistic);

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
                    gear
            );
        }

        public MonsterWithGearDto mapToMonsterWithGearDto(Monster monster) {
            Statistics baseStatistic = monster.getStatistics();
            Statistics gearStatistic = Statistics.zero();
            Statistics sumStatistics = new Statistics().mergeWith(baseStatistic).mergeWith(gearStatistic);

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
                    new MonsterGearDto()
            );
        }
    }


    interface Repository extends ReactiveMongoRepository<Monster, ObjectId> {
        Flux<Monster> findAllBy(Pageable pageable);
    }
}
