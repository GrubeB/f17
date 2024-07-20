package pl.app.comment.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.comment.Comment;
import pl.app.comment.CommentContainer;
import pl.app.comment.CommentQueryService;
import pl.app.comment.CommentService;
import pl.app.comment.command.*;

@Service
@RequiredArgsConstructor
class CommentServiceImpl implements CommentService {

    private final MongoTemplate template;
    private final CommentQueryService commentQueryService;

    @Override
    public CommentContainer createCommentContainer(@Valid CreateCommentContainerCommand command) {
        //TODO without duplicates
        CommentContainer commentContainer = new CommentContainer(command.getDomainObjectType(), command.getDomainObjectId());
        CommentContainer savedCommentContainer = template.insert(commentContainer);
        return savedCommentContainer;
    }

    @Override
    public Comment addComment(@Valid AddCommentCommand command) {
        CommentContainer commentContainer =  commentQueryService.findCommentContainerByDomainObject(command.getDomainObjectId(), command.getDomainObjectType());
        Comment newComment = new Comment(command.getContent(), command.getUserId());
        commentContainer.addComment(newComment);
        template.insert(newComment);
        template.save(commentContainer);
        return newComment;
    }

    @Override
    public Comment addReply(@Valid AddReplyCommand command) {
        Comment parentComment = commentQueryService.findCommentById(command.getParentCommentId());
        Comment newComment = new Comment(command.getContent(), command.getUserId());
        parentComment.addComment(newComment);
        template.insert(newComment);
        template.save(parentComment);
        return newComment;
    }

    @Override
    public void updateComment(@Valid UpdateCommentCommand command) {
        Comment comment = commentQueryService.findCommentById(command.getCommentId());
        comment.setContent(comment.getContent());
        comment.setUserId(comment.getUserId());
        template.save(comment);
    }

    @Override
    public void deleteComment(@Valid DeleteCommentCommand command) {
        //TODO
    }
}
