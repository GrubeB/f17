package pl.app.comment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.Voting;
import pl.app.comment.application.domain.VotingException;
import pl.app.comment.application.port.out.VotingDomainRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class VotingDomainRepositoryImpl implements VotingDomainRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Voting fetchById(ObjectId id) {
        Query query = Query.query(Criteria
                .where("id").is(id)
        );
        return mongoTemplate.query(Voting.class).matching(query).one()
                .orElseThrow(() -> VotingException.NotFoundVotingException.fromId(id.toString()));
    }

    @Override
    public Voting fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return mongoTemplate.query(Voting.class).matching(query).one()
                .orElseThrow(() -> VotingException.NotFoundVotingException.fromDomainObject(domainObjectId, domainObjectType));
    }

    @Override
    public Voting fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType) {
        if (Objects.nonNull(id)) {
            return fetchById(id);
        } else if (Objects.nonNull(domainObjectId) && Objects.nonNull(domainObjectType)) {
            return fetchByDomainObject(domainObjectId, domainObjectType);
        }
        throw new VotingException.NotFoundVotingException();
    }
}
