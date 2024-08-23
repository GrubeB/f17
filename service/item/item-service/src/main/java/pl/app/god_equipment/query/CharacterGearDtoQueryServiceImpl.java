package pl.app.god_equipment.query;

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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.god_equipment.application.domain.CharacterGear;
import pl.app.god_equipment.application.domain.GodEquipment;
import pl.app.god_equipment.dto.CharacterGearDto;
import pl.app.god_equipment.dto.GodEquipmentDto;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class CharacterGearDtoQueryServiceImpl implements CharacterGearDtoQueryService {
    private final ReactiveMongoTemplate mongoTemplate;
    private final Mapper mapper;
    private final Repository repository;

    public CharacterGearDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<CharacterGearDto> fetchById(@NotNull ObjectId id) {
        return repository.findByCharacterGears_CharacterId(id)
                .map(ge -> ge.getCharacterGearById(id).get())
                .map(e -> mapper.map(e, CharacterGearDto.class));
    }

    @Override
    public Mono<Page<CharacterGearDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        return repository.findAllByCharacterGears_CharacterId(ids, pageable)
                .map(ge -> ge.getCharacterGears()
                        .stream()
                        .filter(c -> ids.contains(c.getCharacterId()))
                        .collect(Collectors.toSet())
                ).flatMap(Flux::fromIterable)
                .map(e -> mapper.map(e, CharacterGearDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(CharacterGear.class, CharacterGearDto.class, this::mapToCharacterGearDto);
        }

        CharacterGearDto mapToCharacterGearDto(CharacterGear domain) {
            return new CharacterGearDto(
                    domain.getCharacterId(),
                    domain.getHelmet() != null ? modelMapper.map(domain.getHelmet(), OutfitDto.class) : null,
                    domain.getArmor() != null ? modelMapper.map(domain.getArmor(), OutfitDto.class) : null,
                    domain.getGloves() != null ? modelMapper.map(domain.getGloves(), OutfitDto.class) : null,
                    domain.getBoots() != null ? modelMapper.map(domain.getBoots(), OutfitDto.class) : null,
                    domain.getBelt() != null ? modelMapper.map(domain.getBelt(), OutfitDto.class) : null,
                    domain.getRing() != null ? modelMapper.map(domain.getRing(), OutfitDto.class) : null,
                    domain.getAmulet() != null ? modelMapper.map(domain.getAmulet(), OutfitDto.class) : null,
                    domain.getTalisman() != null ? modelMapper.map(domain.getTalisman(), OutfitDto.class) : null,

                    domain.getLeftHand() != null ? modelMapper.map(domain.getLeftHand(), WeaponDto.class) : null,
                    domain.getRightHand() != null ? modelMapper.map(domain.getRightHand(), WeaponDto.class) : null
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<GodEquipment, ObjectId> {
        @Query("{ 'characterGears.characterId': ?0 }")
        Mono<GodEquipment> findByCharacterGears_CharacterId(ObjectId characterId);

        @Query("{ 'characterGears.characterId': { $in: ?0 } }")
        Flux<GodEquipment> findAllByCharacterGears_CharacterId(List<ObjectId> characterIds, Pageable pageable);
    }
}
