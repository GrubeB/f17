package pl.app.family.query;

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
import pl.app.character_status.application.domain.CharacterStatusType;
import pl.app.character_status.query.CharacterStatusQueryService;
import pl.app.character_status.query.dto.CharacterStatusDto;
import pl.app.common.mapper.BaseMapper;
import pl.app.equipment.dto.EquipmentDto;
import pl.app.family.application.domain.Family;
import pl.app.family.query.dto.FamilyWithGearDto;
import pl.app.gear.dto.GearDto;
import pl.app.item.http.EquipmentQueryControllerHttpInterface;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class FamilyWithGearQueryServiceImpl implements FamilyWithGearDtoQueryService {
    private final Repository repository;
    private final EquipmentQueryControllerHttpInterface godEquipmentQueryController;
    private final CharacterStatusQueryService characterStatusQueryService;
    private final Mapper mapper;


    public FamilyWithGearQueryServiceImpl(Mapper mapper, CharacterStatusQueryService characterStatusQueryService, EquipmentQueryControllerHttpInterface godEquipmentQueryController, ReactiveMongoTemplate mongoTemplate) {
        this.mapper = mapper;
        this.godEquipmentQueryController = godEquipmentQueryController;
        this.characterStatusQueryService = characterStatusQueryService;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<FamilyWithGearDto> fetchByGodId(@NotNull ObjectId godId) {
        return Mono.zip(
                        repository.findByGodId(godId),
                        godEquipmentQueryController.fetchByGodId(godId).map(HttpEntity::getBody)
                )
                .flatMap(t -> {
                    var characterIds = t.getT1().getCharacters().stream().map(Character::getId).toList();
                    var characterStatuses = characterStatusQueryService.fetchAllByCharacterIds(characterIds);
                    return characterStatuses.map(e -> Tuples.of(t.getT1(), t.getT2(), e));
                })
                .map(t -> mapper.mapToGodFamilyWithGearDto(t.getT1(), t.getT2(), t.getT3()));
    }

    @Override
    public Mono<Page<FamilyWithGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable).collect(Collectors.toSet())
                .flatMap(families -> {
                    var godIds = families.stream().map(Family::getGodId).toList();
                    return this.fetchAllByGodIds(godIds, pageable);
                });
    }

    @Override
    public Mono<Page<FamilyWithGearDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable) {
        if (Objects.isNull(godIds)) {
            return fetchAllByPageable(pageable);
        }
        return Mono.zip(
                        repository.findAllByGodId(godIds, pageable).collect(Collectors.toSet()),
                        godEquipmentQueryController
                                .fetchAllByGodIds(godIds)
                                .map(HttpEntity::getBody)
                                .map(Streamable::toSet)
                )
                .flatMap(t -> {
                    var characterIds = t.getT1().stream().map(Family::getCharacters)
                            .map(characters -> characters.stream().map(Character::getId).toList())
                            .flatMap(List::stream).toList();
                    var characterStatuses = characterStatusQueryService.fetchAllByCharacterIds(characterIds);
                    return characterStatuses.map(e -> Tuples.of(t.getT1(), t.getT2(), e));
                })
                .map(t -> mapper.mapToGodFamilyWithGearDto(t.getT1(), t.getT2(), t.getT3()))
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

        public List<FamilyWithGearDto> mapToGodFamilyWithGearDto(Set<Family> families, Set<EquipmentDto> equipments, Collection<CharacterStatusDto> statuses) {
            return families.stream().map(family -> {
                var equipment = equipments.stream().filter(e -> e.getGodId().equals(family.getGodId())).findAny().orElse(new EquipmentDto());
                return mapToGodFamilyWithGearDto(family, equipment, statuses);
            }).collect(Collectors.toList());
        }

        public FamilyWithGearDto mapToGodFamilyWithGearDto(Family family, EquipmentDto equipment, Collection<CharacterStatusDto> statuses) {
            return new FamilyWithGearDto(
                    family.getGodId(),
                    mapToCharacterWithGearDto(family.getCharacters(), equipment.getCharacterGears(), statuses)
            );
        }

        public List<CharacterWithGearDto> mapToCharacterWithGearDto(Collection<Character> characters, Collection<GearDto> gears, Collection<CharacterStatusDto> statuses) {
            return characters.stream().map(character -> {
                var gear = gears.stream().filter(g -> g.getId().equals(character.getId())).findAny().orElse(new GearDto());
                var status = statuses.stream().filter(g -> g.getCharacterId().equals(character.getId())).findAny().orElse(new CharacterStatusDto(null, CharacterStatusType.IDLE));
                return map(character, gear, status, CharacterWithGearDto.class);
            }).collect(Collectors.toList());
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
