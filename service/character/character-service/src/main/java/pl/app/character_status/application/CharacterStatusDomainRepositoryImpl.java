package pl.app.character_status.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.character_status.application.domain.CharacterStatus;
import pl.app.character_status.application.port.in.CharacterStatusDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CharacterStatusDomainRepositoryImpl implements CharacterStatusDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<CharacterStatus> fetchByCharacterId(ObjectId characterId) {
        Query query = Query.query(Criteria.where("characterId").is(characterId));
        return mongoTemplate.query(CharacterStatus.class).matching(query).one();
    }
}
