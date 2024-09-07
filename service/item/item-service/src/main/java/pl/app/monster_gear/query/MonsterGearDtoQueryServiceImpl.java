package pl.app.monster_gear.query;

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
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;
import pl.app.monster_gear.application.domain.MonsterGear;
import pl.app.monster_gear.dto.MonsterGearDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class MonsterGearDtoQueryServiceImpl implements MonsterGearDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public MonsterGearDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<MonsterGearDto> fetchByMonsterId(@NonNull ObjectId monsterId) {
        return repository.findByMonsterId(monsterId)
                .map(e -> mapper.map(e, MonsterGearDto.class));
    }

    @Override
    public Mono<Page<MonsterGearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, MonsterGearDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<MonsterGearDto>> fetchAllByMonsterIds(List<ObjectId> monsterIds, Pageable pageable) {
        if (Objects.isNull(monsterIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllByMonsterIds(monsterIds, pageable)
                .map(e -> mapper.map(e, MonsterGearDto.class))
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
            addMapper(MonsterGear.class, MonsterGearDto.class, this::mapToMonsterGearDto);
        }

        MonsterGearDto mapToMonsterGearDto(MonsterGear domain) {
            return new MonsterGearDto(
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
                    domain.getMonsterId()
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<MonsterGear, ObjectId> {

        @Query("{ 'monsterId': ?0 }")
        Mono<MonsterGear> findByMonsterId(ObjectId id);

        Flux<MonsterGear> findAllBy(Pageable pageable);

        @Query("{ 'monsterId': { $in: ?0 } }")
        Flux<MonsterGear> findAllByMonsterIds(List<ObjectId> ids, Pageable pageable);
    }
}
