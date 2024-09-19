package pl.app.god.application;

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
import pl.app.god.application.port.in.GodDomainRepository;
import reactor.core.publisher.Mono;

;

@Component
@RequiredArgsConstructor
class GodDomainDomainRepositoryImpl implements
        GodDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(GodDomainDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<God> fetchById(ObjectId id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.query(God.class).matching(query).one()
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> GodException.NotFoundGodException.fromId(id.toHexString()))))
                .doOnError(e -> logger.error("error occurred while fetching god with id: {}: {}", id, e.toString()));
    }
}
