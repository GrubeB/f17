package pl.app.god_family.adapter.out;

import god_family.application.domain.GodFamily;
import god_family.application.domain.GodFamilyException;
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
import pl.app.character.application.port.out.CharacterDomainRepository;
import pl.app.god_family.application.port.out.GodFamilyDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class GodFamilyDomainRepositoryImpl implements GodFamilyDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(GodFamilyDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<GodFamily> fetchByGodId(ObjectId godId) {
        Query query = Query.query(Criteria.where("godId").is(godId));
        return mongoTemplate.query(GodFamily.class).matching(query).one()
                .doOnNext(domain -> logger.debug("fetched god family with id: {}", domain.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> GodFamilyException.NotFoundGodFamilyException.fromGodId(godId.toString()))))
                .doOnError(e -> logger.error("error occurred while fetching god family with id: {}: {}", godId, e.toString()));
    }
}
