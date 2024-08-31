package pl.app.character.query;

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
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.app.character.application.domain.Character;
import pl.app.character.query.dto.CharacterDto;
import pl.app.common.mapper.BaseMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
class CharacterQueryServiceImpl implements CharacterQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public CharacterQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<CharacterDto> fetchById(@NonNull ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, CharacterDto.class));
    }

    @Override
    public Mono<Page<CharacterDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, CharacterDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<CharacterDto>> fetchAllByIds(List<ObjectId> ids, Pageable pageable) {
        if (Objects.isNull(ids)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllById(ids)
                .map(e -> mapper.map(e, CharacterDto.class))
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
            addMapper(Character.class, CharacterDto.class, e -> modelMapper.map(e, CharacterDto.class));
        }
    }

    interface Repository extends ReactiveMongoRepository<Character, ObjectId> {
        Flux<Character> findAllBy(Pageable pageable);
    }
}
