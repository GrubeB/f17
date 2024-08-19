package pl.app.comment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.voting.application.domain.Voting;
import reactor.core.publisher.Mono;

public interface CommentContainerDomainRepository {
    Mono<CommentContainer> fetchById(ObjectId id);

    Mono<CommentContainer> fetchByCommentId(ObjectId commentId);

    Mono<CommentContainer> fetchByDomainObject(String domainObjectId, String domainObjectType);

    Mono<CommentContainer> fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType);
}
