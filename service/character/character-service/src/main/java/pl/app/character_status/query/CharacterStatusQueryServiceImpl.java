package pl.app.character_status.query;

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
import pl.app.character_status.application.domain.CharacterStatus;
import pl.app.character_status.query.dto.CharacterStatusDto;
import pl.app.common.mapper.BaseMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
class CharacterStatusQueryServiceImpl implements CharacterStatusQueryService {
    private final Mapper mapper;
    private final Repository repository;

    public CharacterStatusQueryServiceImpl(ReactiveMongoTemplate mongoTemplate, Mapper mapper) {
        this.mapper = mapper;
        this.repository = new ReactiveMongoRepositoryFactory(mongoTemplate).getRepository(Repository.class);
    }

    @Override
    public Mono<CharacterStatusDto> fetchByCharacterId(@NonNull ObjectId characterId) {
        return repository.findByCharacterId(characterId)
                .map(e -> mapper.map(e, CharacterStatusDto.class));
    }

    @Override
    public Mono<List<CharacterStatusDto>> fetchAllByCharacterIds(List<ObjectId> characterIds) {
        return repository.findAllByCharacterIds(characterIds)
                .map(e -> mapper.map(e, CharacterStatusDto.class))
                .collectList();
    }

    @Override
    public Mono<Page<CharacterStatusDto>> fetchAllByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, CharacterStatusDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<Page<CharacterStatusDto>> fetchAllByCharacterIds(List<ObjectId> characterIds, Pageable pageable) {
        if (Objects.isNull(characterIds)) {
            return fetchAllByPageable(pageable);
        }
        return repository.findAllByCharacterIds(characterIds, pageable)
                .map(e -> mapper.map(e, CharacterStatusDto.class))
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
            addMapper(CharacterStatus.class, CharacterStatusDto.class, e -> modelMapper.map(e, CharacterStatusDto.class));
        }
    }

    interface Repository extends ReactiveMongoRepository<CharacterStatus, ObjectId> {
        @Query("{ 'characterId': ?0 }")
        Mono<CharacterStatus> findByCharacterId(ObjectId id);

        Flux<CharacterStatus> findAllBy(Pageable pageable);

        @Query("{ 'characterId': { $in: ?0 } }")
        Flux<CharacterStatus> findAllByCharacterIds(List<ObjectId> ids, Pageable pageable);

        @Query("{ 'characterId': { $in: ?0 } }")
        Flux<CharacterStatus> findAllByCharacterIds(List<ObjectId> ids);
    }
}
