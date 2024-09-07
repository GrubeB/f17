package pl.app.god_equipment.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.lang.NonNull;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
class CharacterGearDtoQueryServiceImpl implements CharacterGearDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public CharacterGearDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<CharacterGearDto> fetchByCharacterId(@NonNull ObjectId characterId) {
        return repository.findByCharacterId(characterId)
                .map(e -> mapper.map(e, CharacterGearDto.class));
    }

    @Override
    public Mono<Page<CharacterGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, CharacterGearDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<CharacterGearDto>> fetchAllByCharacterIds(List<ObjectId> characterIds, Pageable pageable) {
        if (Objects.isNull(characterIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllByCharacterId(characterIds, pageable)
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
                    domain.getHelmet() != null ? modelMapper.map(domain.getHelmet(), OutfitDto.class) : null,
                    domain.getArmor() != null ? modelMapper.map(domain.getArmor(), OutfitDto.class) : null,
                    domain.getGloves() != null ? modelMapper.map(domain.getGloves(), OutfitDto.class) : null,
                    domain.getBoots() != null ? modelMapper.map(domain.getBoots(), OutfitDto.class) : null,
                    domain.getBelt() != null ? modelMapper.map(domain.getBelt(), OutfitDto.class) : null,
                    domain.getRing() != null ? modelMapper.map(domain.getRing(), OutfitDto.class) : null,
                    domain.getAmulet() != null ? modelMapper.map(domain.getAmulet(), OutfitDto.class) : null,
                    domain.getTalisman() != null ? modelMapper.map(domain.getTalisman(), OutfitDto.class) : null,

                    domain.getLeftHand() != null ? modelMapper.map(domain.getLeftHand(), WeaponDto.class) : null,
                    domain.getRightHand() != null ? modelMapper.map(domain.getRightHand(), WeaponDto.class) : null,
                    domain.getCharacterId()
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<CharacterGear, ObjectId> {

        @Query("{ 'characterId': ?0 }")
        Mono<CharacterGear> findByCharacterId(ObjectId id);

        Flux<CharacterGear> findAllBy(Pageable pageable);

        @Query("{ 'godId.': { $in: ?0 } }")
        Flux<CharacterGear> findAllByCharacterId(List<ObjectId> ids, Pageable pageable);
    }
}
