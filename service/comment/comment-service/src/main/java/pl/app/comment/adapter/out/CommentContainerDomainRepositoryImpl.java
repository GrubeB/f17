package pl.app.comment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.domain.CommentException;
import pl.app.comment.application.port.out.CommentContainerDomainRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
class CommentContainerDomainRepositoryImpl implements CommentContainerDomainRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public CommentContainer fetchById(ObjectId id) {
        Query query = Query.query(Criteria
                .where("id").is(id)
        );
        return mongoTemplate.query(CommentContainer.class).matching(query).one()
                .orElseThrow(() -> CommentException.NotFoundCommentContainerException.fromId(id.toString()));
    }

    @Override
    public CommentContainer fetchByCommentId(ObjectId commentId) {
        Query query = Query.query(Criteria
                .where("id").is(commentId)
        );
        Comment comment = mongoTemplate.query(Comment.class).matching(query).one()
                .orElseThrow(() -> CommentException.NotFoundCommentException.fromId(commentId.toString()));
        return comment.getCommentContainer();
    }

    @Override
    public CommentContainer fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return mongoTemplate.query(CommentContainer.class).matching(query).one()
                .orElseThrow(() -> CommentException.NotFoundCommentContainerException.fromDomainObject(domainObjectId, domainObjectType));
    }

    @Override
    public CommentContainer fetchByIdOrDomainObject(ObjectId id, String domainObjectId, String domainObjectType) {
        if (Objects.nonNull(id)) {
            return fetchById(id);
        } else if (Objects.nonNull(domainObjectId) && Objects.nonNull(domainObjectType)) {
            return fetchByDomainObject(domainObjectId, domainObjectType);
        }
        throw new CommentException.NotFoundCommentException();
    }
}
