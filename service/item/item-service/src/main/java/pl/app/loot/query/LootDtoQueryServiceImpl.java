package pl.app.loot.query;

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
import pl.app.common.shared.model.ItemType;
import pl.app.item_template.query.dto.ItemTemplateDto;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import pl.app.loot.aplication.domain.Loot;
import pl.app.loot.aplication.domain.LootItem;
import pl.app.loot.dto.LootDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
class LootDtoQueryServiceImpl implements LootDtoQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public LootDtoQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<LootDto> fetchByDomainObject(@NotNull ObjectId domainObjectId, @NotNull Loot.LootDomainObjectType domainObjectType) {
        return repository.fetchByDomainObject(domainObjectId, domainObjectType)
                .map(e -> mapper.map(e, LootDto.class));
    }

    @Override
    public Mono<Page<LootDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, LootDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<LootDto>> fetchAllByDomainObjectIds(List<ObjectId> domainObjectIds, @NotNull Loot.LootDomainObjectType domainObjectType, Pageable pageable) {
        if (Objects.isNull(domainObjectIds) || domainObjectIds.isEmpty()) {
            return this.fetchAllByPageable(pageable);
        }
        return repository.fetchAllByDomainObjectIds(domainObjectIds, domainObjectType, pageable)
                .map(e -> mapper.map(e, LootDto.class))
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
            addMapper(Loot.class, LootDto.class, this::mapToLootDto);
        }

        LootDto mapToLootDto(Loot domain) {
            return new LootDto(
                    domain.getDomainObjectId(),
                    domain.getMoney(),
                    domain.getItems().stream().map(this::mapToLootItemDto).collect(Collectors.toSet()),
                    domain.getItems().stream().filter(e -> ItemType.isOutfit(e.getItemTemplate().getType())).map(this::mapToLootOutfitDto).collect(Collectors.toSet()),
                    domain.getItems().stream().filter(e -> ItemType.isWeapon(e.getItemTemplate().getType())).map(this::mapToLootWeaponDto).collect(Collectors.toSet())
            );
        }

        LootDto.LootItemDto mapToLootItemDto(LootItem domain) {
            return new LootDto.LootItemDto(
                    modelMapper.map(domain.getItemTemplate(), ItemTemplateDto.class),
                    domain.getChance(),
                    domain.getAmount()
            );
        }

        LootDto.LootOutfitDto mapToLootOutfitDto(LootItem domain) {
            return new LootDto.LootOutfitDto(
                    modelMapper.map(domain.getItemTemplate(), OutfitTemplateDto.class),
                    domain.getChance(),
                    domain.getAmount()
            );
        }

        LootDto.LootWeaponDto mapToLootWeaponDto(LootItem domain) {
            return new LootDto.LootWeaponDto(
                    modelMapper.map(domain.getItemTemplate(), WeaponTemplateDto.class),
                    domain.getChance(),
                    domain.getAmount()
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<Loot, ObjectId> {
        @Query("{ 'domainObjectId': ?0, 'domainObjectType': ?1}")
        Mono<Loot> fetchByDomainObject(ObjectId domainObjectId, Loot.LootDomainObjectType domainObjectType);

        @Query("{ 'domainObjectId': { $in: ?0 }, 'domainObjectType': ?1}")
        Flux<Loot> fetchAllByDomainObjectIds(List<ObjectId> ids, Loot.LootDomainObjectType domainObjectType, Pageable pageable);

        Flux<Loot> findAllBy(Pageable pageable);
    }
}
