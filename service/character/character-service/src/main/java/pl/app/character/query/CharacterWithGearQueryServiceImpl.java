package pl.app.character.query;

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
import pl.app.character.application.domain.Character;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.character.query.dto.LevelDto;
import pl.app.common.mapper.BaseMapper;
import pl.app.common.shared.model.Statistics;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.dto.GearDto;
import pl.app.item.http.GearDtoQueryControllerHttpInterface;
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
class CharacterWithGearQueryServiceImpl implements CharacterWithGearDtoQueryService {
    private final Repository repository;
    private final GearDtoQueryControllerHttpInterface GearDtoQueryController;
    private final Mapper mapper;


    public CharacterWithGearQueryServiceImpl(Mapper mapper, GearDtoQueryControllerHttpInterface GearDtoQueryController, ReactiveMongoTemplate mongoTemplate) {
        this.GearDtoQueryController = GearDtoQueryController;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }


    @Override
    public Mono<CharacterWithGearDto> fetchById(@NotNull ObjectId id) {
        return repository.findById(id)
                .zipWith(GearDtoQueryController.fetchByDomainObject(id, Gear.LootDomainObjectType.CHARACTER).map(HttpEntity::getBody))
                .map(t -> mapper.mapToCharacterWithGearDto(t.getT1(), t.getT2()));
    }

    @Override
    public Mono<Page<CharacterWithGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collect(Collectors.toSet())
                .map(set -> GearDtoQueryController
                        .fetchByDomainObject(Gear.LootDomainObjectType.CHARACTER, set.stream().map(Character::getId).collect(Collectors.toList()))
                        .map(HttpEntity::getBody)
                        .map(Streamable::get)
                        .map(s -> s.collect(Collectors.toSet()))
                        .map(g -> Tuples.of(set, g)))
                .flatMap(Function.identity())
                .map(t -> mapper.mapToCharacterWithGearDto(t.getT1(), t.getT2()))
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<CharacterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllById(ids)
                .collect(Collectors.toSet())
                .zipWith(GearDtoQueryController
                        .fetchByDomainObject(Gear.LootDomainObjectType.CHARACTER, ids)
                        .map(HttpEntity::getBody)
                        .map(Streamable::get)
                        .map(s -> s.collect(Collectors.toSet()))

                ).map(t -> mapper.mapToCharacterWithGearDto(t.getT1(), t.getT2()))
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
        public List<CharacterWithGearDto> mapToCharacterWithGearDto(Set<Character> characters, Set<GearDto> gears){
            return characters.stream().map(character -> {
                Optional<GearDto> gear = gears.stream().filter(g -> g.getId().equals(character.getId())).findAny();
                return gear.map(characterGearDto -> mapToCharacterWithGearDto(character, characterGearDto)).orElse(mapToCharacterWithGearDto(character));
            }).collect(Collectors.toList());
        }
        public CharacterWithGearDto mapToCharacterWithGearDto(Character character, GearDto gear) {
            Statistics baseStatistic = character.getStatistics();
            Statistics gearStatistic = gear.getStatistic();
            Statistics sumStatistics = new Statistics().add(baseStatistic).add(gearStatistic);

            return new CharacterWithGearDto(
                    character.getId(),
                    character.getName(),
                    character.getProfession(),
                    character.getRace(),
                    character.getImageId(),
                    new LevelDto(character.getLevel().getLevel(), character.getLevel().getExp()),
                    baseStatistic, gearStatistic, sumStatistics,
                    Statistics.getHp(sumStatistics.getPersistence(), character.getProfession()),
                    Statistics.getDef(sumStatistics.getDurability(), character.getProfession()),
                    Statistics.getAttackPower(sumStatistics.getStrength(), character.getProfession()),
                    gear
            );
        }
        public CharacterWithGearDto mapToCharacterWithGearDto(Character character) {
            Statistics baseStatistic = character.getStatistics();
            Statistics gearStatistic = Statistics.zero();
            Statistics sumStatistics = new Statistics().add(baseStatistic).add(gearStatistic);

            return new CharacterWithGearDto(
                    character.getId(),
                    character.getName(),
                    character.getProfession(),
                    character.getRace(),
                    character.getImageId(),
                    new LevelDto(character.getLevel().getLevel(), character.getLevel().getExp()),
                    baseStatistic, gearStatistic, sumStatistics,
                    Statistics.getHp(sumStatistics.getPersistence(), character.getProfession()),
                    Statistics.getDef(sumStatistics.getDurability(), character.getProfession()),
                    Statistics.getAttackPower(sumStatistics.getStrength(), character.getProfession()),
                    new GearDto()
            );
        }
    }


    interface Repository extends ReactiveMongoRepository<Character, ObjectId> {
        Flux<Character> findAllBy(Pageable pageable);
    }
}
