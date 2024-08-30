package pl.app.god.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.god.application.domain.God;
import pl.app.god.application.domain.GodException;
import reactor.core.publisher.Mono;

;

@Component
@RequiredArgsConstructor
class GodDomainRepositoryImpl implements
        pl.app.god.application.port.out.GodDomainRepository,
        pl.app.recruitment.application.port.out.GodRepository {
    private static final Logger logger = LoggerFactory.getLogger(GodDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<God> fetchById(ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(God.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> GodException.NotFoundGodException.fromId(id.toHexString()))))
                .doOnError(e -> logger.error("error occurred while fetching god with id: {}: {}", id, e.toString()));
    }
}
