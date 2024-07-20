package pl.app.comment.impl;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.comment.Comment;
import pl.app.comment.CommentContainer;
import pl.app.comment.CommentException;
import pl.app.comment.CommentQueryService;

@Service
@RequiredArgsConstructor
class CommentQueryServiceImpl implements CommentQueryService {
    private final MongoTemplate template;

    @Override
    public CommentContainer findCommentContainerById(ObjectId id) {
        Query query = Query.query(Criteria
                .where("id").is(id)
        );
        return template.query(CommentContainer.class).matching(query).one()
                .orElseThrow(() -> CommentException.NotFoundCommentContainerException.fromId(id.toString()));
    }

    @Override
    public CommentContainer findCommentContainerByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return template.query(CommentContainer.class).matching(query).one()
                .orElseThrow(() -> CommentException.NotFoundCommentContainerException.fromDomainObject(domainObjectId, domainObjectType));
    }


    @Override
    public Comment findCommentById(ObjectId id) {
        Query query = Query.query(Criteria
                .where("id").is(id)
        );
        return template.query(Comment.class).matching(query).one()
                .orElseThrow(() -> CommentException.NotFoundCommentException.fromId(id.toString()));
    }
}
