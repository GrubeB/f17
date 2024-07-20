package pl.app.comment;

import org.bson.types.ObjectId;

public interface CommentQueryService {
    CommentContainer findCommentContainerByDomainObject(String domainObjectId, String domainObjectType);
    CommentContainer findCommentContainerById(ObjectId id);

    Comment findCommentById(ObjectId id);
}
