package pl.app.gear.query;

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
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.dto.GearDto;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class GearDtoQueryServiceImpl implements GearDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public GearDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<GearDto> fetchByDomainObject(@NotNull ObjectId domainObjectId, @NotNull Gear.LootDomainObjectType domainObjectType) {
        return repository.fetchByDomainObject(domainObjectId, domainObjectType)
                .map(e -> mapper.map(e, GearDto.class));
    }

    @Override
    public Mono<Page<GearDto>> fetchAllByDomainObjectIds(List<ObjectId> domainObjectIds, @NotNull Gear.LootDomainObjectType domainObjectType, Pageable pageable) {
        if (Objects.isNull(domainObjectIds) || domainObjectIds.isEmpty()) {
            return this.fetchAllByPageable(pageable);
        }
        return repository.fetchAllByDomainObjectIds(domainObjectIds, domainObjectType, pageable)
                .map(e -> mapper.map(e, GearDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<GearDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, GearDto.class))
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
            addMapper(Gear.class, GearDto.class, this::mapToMonsterGearDto);
        }

        GearDto mapToMonsterGearDto(Gear domain) {
            return new GearDto(
                    domain.getDomainObjectId(),
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

    interface Repository extends ReactiveMongoRepository<Gear, ObjectId> {
        @Query("{ 'domainObjectId': ?0, 'domainObjectType': ?1}")
        Mono<Gear> fetchByDomainObject(ObjectId domainObjectId, Gear.LootDomainObjectType domainObjectType);

        @Query("{ 'domainObjectId': { $in: ?0 }, 'domainObjectType': ?1}")
        Flux<Gear> fetchAllByDomainObjectIds(List<ObjectId> ids, Gear.LootDomainObjectType domainObjectType, Pageable pageable);

        Flux<Gear> findAllBy(Pageable pageable);
    }
}
