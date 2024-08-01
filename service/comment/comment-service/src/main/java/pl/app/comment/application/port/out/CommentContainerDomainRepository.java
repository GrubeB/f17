package pl.app.comment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.comment.application.domain.CommentContainer;

public interface CommentContainerDomainRepository {
    CommentContainer fetchById(ObjectId id);

    CommentContainer fetchByCommentId(ObjectId commentId);

    CommentContainer fetchByDomainObject(String domainObjectId, String domainObjectType);

    CommentContainer fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType);
}
