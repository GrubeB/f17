package pl.app.comment.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.domain.CommentException;
import pl.app.comment.application.port.out.CommentContainerRepository;
import pl.app.comment.application.port.out.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
class CommentContainerQueryServiceImpl implements CommentContainerQueryService {
    private final MongoTemplate template;
    private final CommentContainerRepository repository;
    private final CommentRepository commentRepository;

    @Override
    public List<CommentContainer> fetchAll() {
        return repository.findAll();
    }

    @Override
    public Page<CommentContainer> fetchByPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public CommentContainer fetchById(ObjectId id) {
        return repository.findById(id)
                .orElseThrow(() -> CommentException.NotFoundCommentContainerException.fromId(id.toString()));
    }

    @Override
    public CommentContainer fetchByDomainObject(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return template.query(CommentContainer.class).matching(query).one()
                .orElseThrow(() -> CommentException.NotFoundCommentContainerException.fromDomainObject(domainObjectId, domainObjectType));
    }

    @Override
    public CommentContainer fetchByCommentId(ObjectId commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> CommentException.NotFoundCommentException.fromId(commentId.toString()));
        return comment.getCommentContainer();
    }

    @Override
    public List<CommentContainer> fetchByIds(List<ObjectId> ids) {
        return repository.findAllById(ids);
    }
}
