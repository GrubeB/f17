package pl.app.item_template.query;

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
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.query.dto.OutfitTemplateDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class OutfitTemplateQueryServiceImpl implements OutfitTemplateQueryService {
    private final Mapper mapper;
    private final Repository repository;


    public OutfitTemplateQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<OutfitTemplateDto> fetchById(@NonNull ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, OutfitTemplateDto.class));
    }

    @Override
    public Mono<Page<OutfitTemplateDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, OutfitTemplateDto.class))
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
            addMapper(OutfitTemplate.class, OutfitTemplateDto.class, e -> modelMapper.map(e, OutfitTemplateDto.class));
        }
    }

    interface Repository extends ReactiveMongoRepository<OutfitTemplate, ObjectId> {
        @Query("{ 'type': { $in : ['HELMET', 'ARMOR', 'GLOVES', 'BOOTS', 'BELT', 'RING', 'AMULET', 'TALISMAN']} }")
        Flux<OutfitTemplate> findAllBy(Pageable pageable);
    }
}
