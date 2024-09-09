package pl.app.equipment.query;

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
import pl.app.equipment.application.domain.Equipment;
import pl.app.equipment.dto.EquipmentDto;
import pl.app.gear.dto.GearDto;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class EquipmentQueryServiceImpl implements EquipmentQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public EquipmentQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<EquipmentDto> fetchByGodId(@NonNull ObjectId godId) {
        return repository.findByGodId(godId)
                .map(e -> mapper.map(e, EquipmentDto.class));
    }

    @Override
    public Mono<Page<EquipmentDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, EquipmentDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<EquipmentDto>> fetchAllByGodIds(List<ObjectId> godIds, Pageable pageable) {
        if (Objects.isNull(godIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllByGodId(godIds, pageable)
                .map(e -> mapper.map(e, EquipmentDto.class))
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
            addMapper(Equipment.class, EquipmentDto.class, this::mapToGodEquipmentDto);
        }

        EquipmentDto mapToGodEquipmentDto(Equipment domain) {
            Set<OutfitDto> outfits = domain.getUnattachedOutfits().stream()
                    .map(d -> modelMapper.map(d, OutfitDto.class))
                    .collect(Collectors.toSet());
            Set<WeaponDto> weapons = domain.getUnattachedWeapons().stream()
                    .map(d -> modelMapper.map(d, WeaponDto.class))
                    .collect(Collectors.toSet());
            return new EquipmentDto(
                    domain.getGodId(),
                    outfits,
                    weapons,
                    domain.getCharacterGears().stream().map(d -> map(d, GearDto.class)).collect(Collectors.toSet())
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<Equipment, ObjectId> {
        @Query("{ 'godId': ?0 }")
        Mono<Equipment> findByGodId(ObjectId id);

        Flux<Equipment> findAllBy(Pageable pageable);

        @Query("{ 'godId': { $in: ?0 } }")
        Flux<Equipment> findAllByGodId(List<ObjectId> ids, Pageable pageable);
    }
}
