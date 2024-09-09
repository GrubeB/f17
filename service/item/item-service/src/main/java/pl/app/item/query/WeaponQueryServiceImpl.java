package pl.app.item.query;

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
import pl.app.item.application.domain.Weapon;
import pl.app.item.query.dto.WeaponDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class WeaponQueryServiceImpl implements WeaponQueryService {
    private final Mapper mapper;
    private final Repository repository;


    public WeaponQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<WeaponDto> fetchById(@NonNull ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, WeaponDto.class));
    }

    @Override
    public Mono<Page<WeaponDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, WeaponDto.class))
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
            addMapper(Weapon.class, WeaponDto.class, e -> modelMapper.map(e, WeaponDto.class));
        }

        WeaponDto mapToWeaponDto(Weapon domain) {
            return new WeaponDto(
                    domain.getId(),
                    domain.getType(),
                    domain.getTemplateId(),
                    domain.getName(),
                    domain.getDescription(),
                    domain.getImageId(),
                    domain.getMoney(),
                    domain.getQuality(),
                    domain.getPersistence(),
                    domain.getDurability(),
                    domain.getStrength(),
                    domain.getSpeed(),
                    domain.getCriticalRate(),
                    domain.getCriticalDamage(),
                    domain.getAccuracy(),
                    domain.getResistance(),
                    domain.getMinDmg(),
                    domain.getMaxDmg()
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<Weapon, ObjectId> {
        @Query("{ 'template.type': { $in : ['AXE', 'CLUB', 'SWORD', 'BOOTS', 'WANDS', 'THROWING_WEAPONS', 'BOWS', 'CROSSBOWS']} }")
        Flux<Weapon> findAllBy(Pageable pageable);
    }
}
