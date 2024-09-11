package pl.app.family.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.character.application.domain.Character;
import pl.app.character.application.domain.CharacterException;
import pl.app.family.application.port.out.CharacterDomainRepository;
import reactor.core.publisher.Mono;

@Component("pl.app.god_family.adapter.out.CharacterDomainRepositoryImpl")
@RequiredArgsConstructor
class CharacterDomainRepositoryImpl implements CharacterDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(CharacterDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Character> fetchById(ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(Character.class).matching(query).one()
                .doOnNext(domain -> logger.debug("fetched character with id: {}", domain.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> CharacterException.NotFoundCharacterException.fromId(id.toString()))))
                .doOnError(e -> logger.error("error occurred while fetching character with id: {}: {}", id, e.toString()));
    }
}
