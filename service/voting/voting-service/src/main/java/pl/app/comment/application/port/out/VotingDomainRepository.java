package pl.app.comment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.voting.application.domain.Voting;

public interface VotingDomainRepository {
    Voting fetchById(ObjectId id);

    Voting fetchByDomainObject(String domainObjectId, String domainObjectType);

    Voting fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType);
}
