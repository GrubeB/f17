package pl.app.character.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.character.application.domain.Character;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.character.query.dto.LevelDto;
import pl.app.character_status.application.domain.CharacterStatusType;
import pl.app.character_status.query.CharacterStatusQueryService;
import pl.app.character_status.query.dto.CharacterStatusDto;
import pl.app.common.mapper.BaseMapper;
import pl.app.common.shared.model.Statistics;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.dto.GearDto;
import pl.app.item.http.GearDtoQueryControllerHttpInterface;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
class CharacterWithGearQueryServiceImpl implements CharacterWithGearDtoQueryService {
    private final Repository repository;
    private final GearDtoQueryControllerHttpInterface gearDtoQueryController;
    private final CharacterStatusQueryService characterStatusQueryService;
    private final Mapper mapper;


    public CharacterWithGearQueryServiceImpl(Mapper mapper, CharacterStatusQueryService characterStatusQueryService, GearDtoQueryControllerHttpInterface GearDtoQueryController, ReactiveMongoTemplate mongoTemplate) {
        this.gearDtoQueryController = GearDtoQueryController;
        this.characterStatusQueryService = characterStatusQueryService;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }


    @Override
    public Mono<CharacterWithGearDto> fetchById(@NotNull ObjectId id) {
        return Mono.zip(
                repository.findById(id),
                gearDtoQueryController.fetchByDomainObject(id, Gear.LootDomainObjectType.CHARACTER).map(HttpEntity::getBody),
                characterStatusQueryService.fetchByCharacterId(id)
        ).map(t -> mapper.map(t.getT1(), t.getT2(), t.getT3(), CharacterWithGearDto.class));
    }

    @Override
    public Mono<Page<CharacterWithGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable).collect(Collectors.toSet())
                .flatMap(characters -> {
                    var characterIds = characters.stream().map(Character::getId).toList();
                    return Mono.zip(
                            Mono.just(characters),
                            gearDtoQueryController.fetchByDomainObject(Gear.LootDomainObjectType.CHARACTER, characterIds).map(HttpEntity::getBody).map(Page::getContent),
                            characterStatusQueryService.fetchAllByCharacterIds(characterIds, PageRequest.ofSize(Integer.MAX_VALUE)).map(Page::getContent)
                    );
                })
                .map(t -> mapper.mapToCharacterWithGearDto(t.getT1(), t.getT2(), t.getT3()))
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<CharacterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchAllByPageable(pageable);
        }
        return Mono.zip(
                        repository.findAllById(ids).collectList(),
                        gearDtoQueryController.fetchByDomainObject(Gear.LootDomainObjectType.CHARACTER, ids).map(HttpEntity::getBody).map(Page::getContent),
                        characterStatusQueryService.fetchAllByCharacterIds(ids, PageRequest.ofSize(Integer.MAX_VALUE)).map(Page::getContent)
                )
                .map(t -> mapper.mapToCharacterWithGearDto(t.getT1(), t.getT2(), t.getT3()))
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(Character.class, CharacterWithGearDto.class, this::mapToCharacterWithGearDto);
            addMapper(Character.class, GearDto.class, CharacterWithGearDto.class, this::mapToCharacterWithGearDto);
            addMapper(Character.class, GearDto.class, CharacterStatusDto.class, CharacterWithGearDto.class, this::mapToCharacterWithGearDto);
        }

        public List<CharacterWithGearDto> mapToCharacterWithGearDto(Collection<Character> characters, Collection<GearDto> gears, Collection<CharacterStatusDto> statuses) {
            return characters.stream().map(character -> {
                var gear = gears.stream().filter(g -> g.getId().equals(character.getId())).findAny().orElse(new GearDto());
                var status = statuses.stream().filter(g -> g.getCharacterId().equals(character.getId())).findAny().orElse(new CharacterStatusDto(null, CharacterStatusType.IDLE));
                return map(character, gear, status, CharacterWithGearDto.class);
            }).collect(Collectors.toList());
        }

        CharacterWithGearDto mapToCharacterWithGearDto(Character character, GearDto gear, CharacterStatusDto status) {
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
                    gear,
                    status.getType()
            );
        }

        CharacterWithGearDto mapToCharacterWithGearDto(Character character, GearDto gear) {
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
                    gear,
                    CharacterStatusType.IDLE
            );
        }

        CharacterWithGearDto mapToCharacterWithGearDto(Character character) {
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
                    new GearDto(),
                    CharacterStatusType.IDLE
            );
        }
    }


    interface Repository extends ReactiveMongoRepository<Character, ObjectId> {
        Flux<Character> findAllBy(Pageable pageable);
    }
}
