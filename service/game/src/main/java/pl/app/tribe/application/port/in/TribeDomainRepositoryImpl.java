package pl.app.tribe.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.tribe.application.domain.Tribe;
import pl.app.tribe.application.domain.TribeException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class TribeDomainRepositoryImpl implements TribeDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Tribe> fetchById(ObjectId tribeId) {
        return mongoTemplate.query(Tribe.class)
                .matching(Query.query(Criteria.where("_id").is(tribeId)))
                .one()
                .switchIfEmpty(Mono.error(() -> TribeException.NotFoundTribeException.fromId(tribeId.toHexString())));
    }

}
