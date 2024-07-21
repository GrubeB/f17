package pl.app.comment.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.domain.CommentException;
import pl.app.comment.application.port.in.CommentService;
import pl.app.comment.application.port.in.command.*;
import pl.app.comment.query.CommentContainerQueryService;
import pl.app.comment.query.CommentQueryService;

@Service
@RequiredArgsConstructor
class CommentServiceImpl implements CommentService {

    private final MongoTemplate template;
    private final CommentContainerQueryService commentContainerQueryService;
    private final CommentQueryService commentQueryService;

    @Override
    public CommentContainer createCommentContainer(@Valid CreateCommentContainerCommand command) {
        verifyThereIsNoDuplicates(command.getDomainObjectId(), command.getDomainObjectType());
        CommentContainer commentContainer = new CommentContainer(command.getDomainObjectId(), command.getDomainObjectType());
        CommentContainer savedCommentContainer = template.insert(commentContainer);
        return savedCommentContainer;
    }

    private void verifyThereIsNoDuplicates(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        if (template.query(CommentContainer.class).matching(query).one().isPresent()) {
            throw CommentException.DuplicatedDomainObjectException.fromDomainObject(domainObjectId, domainObjectType);
        }
    }

    @Override
    public Comment addComment(@Valid AddCommentCommand command) {
        CommentContainer commentContainer = commentContainerQueryService.fetchByDomainObject(command.getDomainObjectId(), command.getDomainObjectType());
        Comment newComment = new Comment(command.getContent(), command.getUserId());
        commentContainer.addComment(newComment);
        template.insert(newComment);
        template.save(commentContainer);
        return newComment;
    }

    @Override
    public Comment addReply(@Valid AddReplyCommand command) {
        Comment parentComment = commentQueryService.fetchById(command.getParentCommentId());
        Comment newComment = new Comment(command.getContent(), command.getUserId());
        parentComment.addComment(newComment);
        template.insert(newComment);
        template.save(parentComment);
        return newComment;
    }

    @Override
    public void updateComment(@Valid UpdateCommentCommand command) {
        Comment comment = commentQueryService.fetchById(command.getCommentId());
        comment.setContent(comment.getContent());
        comment.setUserId(comment.getUserId());
        template.save(comment);
    }

    @Override
    public void deleteComment(@Valid DeleteCommentCommand command) {
        Query query = Query.query(Criteria.where("id").is(command.getCommentId()));
        template.query(Comment.class).matching(query).one()
                .ifPresent(comment -> {
                    comment.setDeleteStatus();
                    template.save(comment);
                });
    }
}
