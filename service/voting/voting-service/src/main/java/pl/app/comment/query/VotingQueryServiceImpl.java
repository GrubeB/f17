package pl.app.comment.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.Voting;
import pl.app.comment.application.domain.VotingException;

import java.util.List;

@Service
@RequiredArgsConstructor
class VotingQueryServiceImpl implements VotingQueryService {
    private final MongoTemplate template;
    private final VotingRepository repository;

    @Override
    public List<Voting> fetchAll() {
        return repository.findAll();
    }

    @Override
    public Page<Voting> fetchByPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Voting fetchById(ObjectId id) {
        return repository.findById(id)
                .orElseThrow(() -> VotingException.NotFoundVotingException.fromId(id.toString()));
    }

    @Override
    public Voting fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return template.query(Voting.class).matching(query).one()
                .orElseThrow(() -> VotingException.NotFoundVotingException.fromDomainObject(domainObjectId, domainObjectType));
    }

    @Override
    public List<Voting> fetchByIds(List<ObjectId> ids) {
        return repository.findAllById(ids);
    }
}
