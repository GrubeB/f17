package pl.app.character_template.query;

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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.character_template.application.domain.CharacterTemplate;
import pl.app.character_template.dto.CharacterTemplateDto;
import pl.app.common.mapper.BaseMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class CharacterTemplateQueryServiceImpl implements CharacterTemplateQueryService {
    private final Mapper mapper;
    private final Repository repository;


    public CharacterTemplateQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<CharacterTemplateDto> fetchById(@NonNull ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, CharacterTemplateDto.class));
    }

    @Override
    public Mono<Page<CharacterTemplateDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, CharacterTemplateDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<CharacterTemplateDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllById(ids)
                .map(e -> mapper.map(e, CharacterTemplateDto.class))
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
            addMapper(CharacterTemplate.class, CharacterTemplateDto.class, e -> modelMapper.map(e, CharacterTemplateDto.class));
        }

    }

    interface Repository extends ReactiveMongoRepository<CharacterTemplate, ObjectId> {
        Flux<CharacterTemplate> findAllBy(Pageable pageable);
    }
}
