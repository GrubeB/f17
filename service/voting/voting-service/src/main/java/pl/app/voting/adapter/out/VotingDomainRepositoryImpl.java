package pl.app.voting.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.voting.application.port.out.VotingDomainRepository;
import pl.app.voting.application.domain.Voting;
import pl.app.voting.application.domain.VotingException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class VotingDomainRepositoryImpl implements VotingDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Voting> fetchById(ObjectId id) {
        Query query = Query.query(Criteria
                .where("id").is(id)
        );
        return mongoTemplate.query(Voting.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> VotingException.NotFoundVotingException.fromId(id.toString())));
    }

    @Override
    public Mono<Voting> fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return mongoTemplate.query(Voting.class).matching(query).one()
                .switchIfEmpty(Mono.error(() -> VotingException.NotFoundVotingException.fromDomainObject(domainObjectId, domainObjectType)));
    }

    @Override
    public Mono<Voting> fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType) {
        if (Objects.nonNull(id)) {
            return fetchById(id);
        } else if (Objects.nonNull(domainObjectId) && Objects.nonNull(domainObjectType)) {
            return fetchByDomainObject(domainObjectId, domainObjectType);
        }
        return Mono.error(VotingException.NotFoundVotingException::new);
    }
}
