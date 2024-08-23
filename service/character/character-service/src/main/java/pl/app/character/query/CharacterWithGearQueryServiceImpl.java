package pl.app.character.query;

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
import org.springframework.stereotype.Service;
import pl.app.character.application.domain.Character;
import pl.app.character.query.dto.CharacterWithGearDto;
import pl.app.character.query.dto.LevelDto;
import pl.app.common.shared.model.Statistics;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.item.http.CharacterGearDtoQueryControllerHttpInterface;
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
    private final ReactiveMongoTemplate mongoTemplate;
    private final Repository repository;
    private final CharacterGearDtoQueryControllerHttpInterface characterQueryController;
    private final ModelMapper modelMapper;


    public CharacterWithGearQueryServiceImpl(ModelMapper modelMapper, CharacterGearDtoQueryControllerHttpInterface characterQueryController, ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.modelMapper = modelMapper;
        this.characterQueryController = characterQueryController;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }


    @Override
    public Mono<CharacterWithGearDto> fetchById(@NotNull ObjectId id) {
        return repository.findById(id)
                .zipWith(characterQueryController.fetchById(id).map(HttpEntity::getBody))
                .map(t -> mapToCharacterWithGearDto(t.getT1(), t.getT2()));
    }

    @Override
    public Mono<Page<CharacterWithGearDto>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collect(Collectors.toSet())
                .map(set -> characterQueryController
                        .fetchAllByIds(set.stream().map(Character::getId).collect(Collectors.toList()))
                        .map(HttpEntity::getBody)
                        .map(Streamable::get)
                        .map(s -> s.collect(Collectors.toSet()))
                        .map(g -> Tuples.of(set, g)))
                .flatMap(Function.identity())
                .map(t -> {
                    Set<Character> characters = t.getT1();
                    Set<CharacterGearDto> gears = t.getT2();
                    return characters.stream().map(character -> {
                        Optional<CharacterGearDto> gear = gears.stream().filter(g -> g.getCharacterId().equals(character.getId())).findAny();
                        return gear.map(characterGearDto -> mapToCharacterWithGearDto(character, characterGearDto)).orElse(null);
                    }).collect(Collectors.toList());
                }).zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<CharacterWithGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchByPageable(pageable);
        }
        return repository.findAllById(ids)
                .collect(Collectors.toSet())
                .zipWith(Mono.defer(() -> {
                            Mono<Set<CharacterGearDto>> map = characterQueryController
                                    .fetchAllByIds(ids)
                                    .map(HttpEntity::getBody)
                                    .map(Streamable::get)
                                    .map(s -> s.collect(Collectors.toSet()));
//                            Set<CharacterGearDto> block = map.block();
                            return map;
                        })
                ).map(t -> {
                    Set<Character> characters = t.getT1();
                    Set<CharacterGearDto> gears = t.getT2();
                    return characters.stream().map(character -> {
                        Optional<CharacterGearDto> gear = gears.stream().filter(g -> g.getCharacterId().equals(character.getId())).findAny();
                        return gear.map(characterGearDto -> mapToCharacterWithGearDto(character, characterGearDto)).orElse(null);
                    }).collect(Collectors.toList());
                }).zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    private CharacterWithGearDto mapToCharacterWithGearDto(Character character, CharacterGearDto gear) {
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


    interface Repository extends ReactiveMongoRepository<Character, ObjectId> {

        Flux<Character> findAllBy(Pageable pageable);

        @Query("{ '_id.': { $in: ?0 } }")
        Flux<Character> findAllByGodId(List<ObjectId> ids, Pageable pageable);
    }
}
