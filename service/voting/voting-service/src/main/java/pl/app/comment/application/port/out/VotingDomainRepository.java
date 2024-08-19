package pl.app.comment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.voting.application.domain.Voting;
import reactor.core.publisher.Mono;

public interface VotingDomainRepository {
    Mono<Voting> fetchById(ObjectId id);

    Mono<Voting> fetchByDomainObject(String domainObjectId, String domainObjectType);

    Mono<Voting> fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType);
}
