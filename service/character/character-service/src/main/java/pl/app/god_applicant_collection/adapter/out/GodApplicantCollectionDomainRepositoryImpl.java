package pl.app.god_applicant_collection.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.god_applicant_collection.application.domain.GodApplicantCollection;
import pl.app.god_applicant_collection.application.port.out.GodApplicantCollectionDomainRepository;
import pl.app.god_family.application.domain.GodFamily;
import pl.app.god_family.application.domain.GodFamilyException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class GodApplicantCollectionDomainRepositoryImpl implements GodApplicantCollectionDomainRepository {
    private static final Logger logger = LoggerFactory.getLogger(GodApplicantCollectionDomainRepositoryImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<GodApplicantCollection> fetchByGodId(ObjectId godId) {
        Query query = Query.query(Criteria.where("godId").is(godId));
        return mongoTemplate.query(GodApplicantCollection.class).matching(query).one()
                .doOnNext(domain -> logger.debug("fetched god applicant collection with id: {}", domain.getId()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(() -> GodFamilyException.NotFoundGodFamilyException.fromGodId(godId.toString()))))
                .doOnError(e -> logger.error("error occurred while fetching god applicant collection of god: {}: {}", godId, e.toString()));
    }
}
