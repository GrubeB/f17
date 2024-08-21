package pl.app.item.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.common.mapper.BaseMapper;
import pl.app.item.application.domain.Outfit;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item_template.application.domain.ItemType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class OutfitQueryServiceImpl implements OutfitQueryService {
    private final ReactiveMongoTemplate mongoTemplate;
    private final Mapper mapper;
    private final Repository repository;


    public OutfitQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<Page<OutfitDto>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, OutfitDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<OutfitDto> fetchById(ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, OutfitDto.class));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;


        @PostConstruct
        void init() {
            addMapper(Outfit.class, OutfitDto.class, this::mapToOutfitDto);
        }

        OutfitDto mapToOutfitDto(Outfit domain) {
            return new OutfitDto(
                    domain.getId(),
                    ItemType.WEAPON.name(),
                    domain.getTemplateId(),
                    domain.getName(),
                    domain.getDescription(),
                    domain.getImageId(),
                    domain.getPersistence(),
                    domain.getDurability(),
                    domain.getStrength(),
                    domain.getSpeed(),
                    domain.getCriticalRate(),
                    domain.getCriticalDamage(),
                    domain.getAccuracy(),
                    domain.getResistance()
            );
        }
    }

    interface Repository extends ReactiveMongoRepository<Outfit, ObjectId> {
        Flux<Outfit> findAllBy(Pageable pageable);
    }
}
