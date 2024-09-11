package pl.app.family.adapter.out;

import pl.app.family.application.domain.Family;
import pl.app.family.application.domain.FamilyException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.family.application.port.out.FamilyDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class FamilyDomainRepositoryImpl implements FamilyDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(FamilyDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Family> fetchByGodId(ObjectId godId) {
        Query query = Query.query(Criteria.where("godId").is(godId));
        return mongoTemplate.query(Family.class).matching(query).one()
                .doOnNext(domain -> logger.debug("fetched god family with id: {}", domain.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> FamilyException.NotFoundGodFamilyException.fromGodId(godId.toString()))))
                .doOnError(e -> logger.error("error occurred while fetching god family with id: {}: {}", godId, e.toString()));
    }
}
