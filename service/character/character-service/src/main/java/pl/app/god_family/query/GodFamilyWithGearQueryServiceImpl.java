package pl.app.god_family.query;

import pl.app.god_family.application.domain.GodFamily;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
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
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.god_equipment.dto.GodEquipmentDto;
import pl.app.god_family.query.dto.GodFamilyWithGearDto;
import pl.app.item.http.GodEquipmentQueryControllerHttpInterface;
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
class GodFamilyWithGearQueryServiceImpl implements GodFamilyWithGearDtoQueryService {
    private final Repository repository;
    private final GodEquipmentQueryControllerHttpInterface godEquipmentQueryController;
    private final Mapper mapper;


    public GodFamilyWithGearQueryServiceImpl(Mapper mapper, GodEquipmentQueryControllerHttpInterface godEquipmentQueryController, ReactiveMongoTemplate mongoTemplate) {
        this.mapper = mapper;
        this.godEquipmentQueryController = godEquipmentQueryController;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<GodFamilyWithGearDto> fetchByGodId(@NotNull ObjectId godId) {
        return repository.findByGodId(godId)
                .zipWith(godEquipmentQueryController
                        .fetchByGodId(godId)
                        .log()
                        .map(HttpEntity::getBody)
                ).map(t -> mapper.mapToGodFamilyWithGearDto(t.getT1(), t.getT2()));
    }

    @Override
    public Mono<Page<GodFamilyWithGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collect(Collectors.toSet())
                .map(set -> godEquipmentQueryController
                        .fetchAllByGodIds(set.stream().map(GodFamily::getGodId).collect(Collectors.toList()))
                        .map(HttpEntity::getBody)
                        .map(Streamable::get)
                        .map(s -> s.collect(Collectors.toSet()))
                        .map(g -> Tuples.of(set, g)))
                .flatMap(Function.identity())
                .map(t -> mapper.mapToGodFamilyWithGearDto(t.getT1(), t.getT2()))
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<GodFamilyWithGearDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable) {
        if (Objects.isNull(godIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllById(godIds)
                .collect(Collectors.toSet())
                .zipWith(godEquipmentQueryController
                        .fetchAllByGodIds(godIds)
                        .map(HttpEntity::getBody)
                        .map(Streamable::get)
                        .map(s -> s.collect(Collectors.toSet()))
                ).map(t -> mapper.mapToGodFamilyWithGearDto(t.getT1(), t.getT2()))
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

        public List<GodFamilyWithGearDto> mapToGodFamilyWithGearDto(Set<GodFamily> families, Set<GodEquipmentDto> equipments) {
            return families.stream().map(family -> {
                Optional<GodEquipmentDto> equipment = equipments.stream().filter(e -> e.getGodId().equals(family.getGodId())).findAny();
                return equipment.map(e -> new GodFamilyWithGearDto(
                        family.getId(),
                        family.getGodId(),
                        mapToCharacterWithGearDto(family.getCharacters(), e.getCharacterGears())
                )).orElse(null);
            }).collect(Collectors.toList());
        }

        public GodFamilyWithGearDto mapToGodFamilyWithGearDto(GodFamily family, GodEquipmentDto equipment) {
            return new GodFamilyWithGearDto(
                    family.getId(),
                    family.getGodId(),
                    mapToCharacterWithGearDto(family.getCharacters(), equipment.getCharacterGears())
            );
        }

        public Set<CharacterWithGearDto> mapToCharacterWithGearDto(Set<Character> characters, Set<CharacterGearDto> gears) {
            return characters.stream().map(character -> {
                Optional<CharacterGearDto> gear = gears.stream().filter(g -> g.getCharacterId().equals(character.getId())).findAny();
                return gear.map(characterGearDto -> mapToCharacterWithGearDto(character, characterGearDto)).orElse(null);
            }).collect(Collectors.toSet());
        }

        public CharacterWithGearDto mapToCharacterWithGearDto(Character character, CharacterGearDto gear) {
            Statistics baseStatistic = character.getStatistics().getStatistics();
            Statistics gearStatistic = gear.getStatistic();
            Statistics sumStatistics = new Statistics().mergeWith(baseStatistic).mergeWith(gearStatistic);

            return new CharacterWithGearDto(
                    character.getId(),
                    character.getName(),
                    character.getProfession().name(),
                    new LevelDto(character.getLevel().getLevel(), character.getLevel().getExp()),
                    baseStatistic, gearStatistic, sumStatistics,
                    Character.getHp(sumStatistics.getPersistence(), character.getProfession().name()),
                    Character.getDef(sumStatistics.getDurability(), character.getProfession().name()),
                    Character.getAttackPower(sumStatistics.getStrength(), character.getProfession().name()),
                    gear
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<GodFamily, ObjectId> {

        @Query("{ 'godId': ?0 }")
        Mono<GodFamily> findByGodId(ObjectId id);

        Flux<GodFamily> findAllBy(Pageable pageable);

        @Query("{ 'godId.': { $in: ?0 } }")
        Flux<GodFamily> findAllByGodId(List<ObjectId> ids, Pageable pageable);
    }
}
