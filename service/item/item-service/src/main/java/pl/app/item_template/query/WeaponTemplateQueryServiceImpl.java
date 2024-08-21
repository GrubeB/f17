package pl.app.item_template.query;

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
import pl.app.item_template.application.domain.ItemTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import pl.app.item_template.query.dto.WeaponTemplateDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class WeaponTemplateQueryServiceImpl implements WeaponTemplateQueryService {
    private final ReactiveMongoTemplate mongoTemplate;
    private final Mapper mapper;
    private final Repository repository;


    public WeaponTemplateQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<Page<WeaponTemplateDto>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, WeaponTemplateDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<WeaponTemplateDto> fetchById(ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, WeaponTemplateDto.class));
    }

    @Component
    @RequiredArgsConstructor
    static class Mapper extends BaseMapper {
        private final ModelMapper modelMapper;

        @PostConstruct
        void init() {
            addMapper(WeaponTemplate.class, WeaponTemplateDto.class, e -> modelMapper.map(e, WeaponTemplateDto.class));
        }
    }

    interface Repository extends ReactiveMongoRepository<WeaponTemplate, ObjectId> {
        Flux<WeaponTemplate> findAllBy(Pageable pageable);
    }
}
