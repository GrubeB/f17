package pl.app.family.query;

import pl.app.family.application.domain.Family;
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
import pl.app.equipment.dto.EquipmentDto;
import pl.app.family.query.dto.FamilyWithGearDto;
import pl.app.gear.dto.GearDto;
import pl.app.item.http.EquipmentQueryControllerHttpInterface;
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
class FamilyWithGearQueryServiceImpl implements FamilyWithGearDtoQueryService {
    private final Repository repository;
    private final EquipmentQueryControllerHttpInterface godEquipmentQueryController;
    private final Mapper mapper;


    public FamilyWithGearQueryServiceImpl(Mapper mapper, EquipmentQueryControllerHttpInterface godEquipmentQueryController, ReactiveMongoTemplate mongoTemplate) {
        this.mapper = mapper;
        this.godEquipmentQueryController = godEquipmentQueryController;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<FamilyWithGearDto> fetchByGodId(@NotNull ObjectId godId) {
        return repository.findByGodId(godId)
                .zipWith(godEquipmentQueryController
                        .fetchByGodId(godId)
                        .map(HttpEntity::getBody)
                ).map(t -> mapper.mapToGodFamilyWithGearDto(t.getT1(), t.getT2()));
    }

    @Override
    public Mono<Page<FamilyWithGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collect(Collectors.toSet())
                .map(set -> godEquipmentQueryController
                        .fetchAllByGodIds(set.stream().map(Family::getGodId).collect(Collectors.toList()))
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
    public Mono<Page<FamilyWithGearDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable) {
        if (Objects.isNull(godIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllByGodId(godIds, pageable)
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

        public List<FamilyWithGearDto> mapToGodFamilyWithGearDto(Set<Family> families, Set<EquipmentDto> equipments) {
            return families.stream().map(family -> {
                Optional<EquipmentDto> equipment = equipments.stream().filter(e -> e.getGodId().equals(family.getGodId())).findAny();
                return equipment.map(e -> new FamilyWithGearDto(
                        family.getGodId(),
                        mapToCharacterWithGearDto(family.getCharacters(), e.getCharacterGears())
                )).orElse(null);
            }).collect(Collectors.toList());
        }

        public FamilyWithGearDto mapToGodFamilyWithGearDto(Family family, EquipmentDto equipment) {
            return new FamilyWithGearDto(
                    family.getGodId(),
                    mapToCharacterWithGearDto(family.getCharacters(), equipment.getCharacterGears())
            );
        }

        public Set<CharacterWithGearDto> mapToCharacterWithGearDto(Set<Character> characters, Set<GearDto> gears) {
            return characters.stream().map(character -> {
                Optional<GearDto> gear = gears.stream().filter(g -> g.getId().equals(character.getId())).findAny();
                return gear.map(characterGearDto -> mapToCharacterWithGearDto(character, characterGearDto)).orElse(null);
            }).collect(Collectors.toSet());
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
    }

    interface Repository extends ReactiveMongoRepository<Family, ObjectId> {

        @Query("{ 'godId': ?0 }")
        Mono<Family> findByGodId(ObjectId id);

        Flux<Family> findAllBy(Pageable pageable);

        @Query("{ 'godId': { $in: ?0 } }")
        Flux<Family> findAllByGodId(List<ObjectId> ids, Pageable pageable);
    }
}
