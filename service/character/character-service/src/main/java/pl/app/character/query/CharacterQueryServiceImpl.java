package pl.app.character.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import pl.app.character.query.dto.CharacterDto;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class CharacterQueryServiceImpl implements CharacterQueryService {
    private final CharacterRepository repository;
    private final CharacterMapper mapper;

    @Override
    public Mono<Page<CharacterDto>> fetchByPageable(Pageable pageable) {
        return repository.findAllBy(pageable)
                .map(e -> mapper.map(e, CharacterDto.class))
                .collectList()
                .zipWith(repository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<CharacterDto> fetchById(ObjectId id) {
        return repository.findById(id)
                .map(e -> mapper.map(e, CharacterDto.class));
    }

}
