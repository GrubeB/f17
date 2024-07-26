package pl.app.comment.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.domain.CommentEvent;
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
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @Value("${app.kafka.topic.comment-container-created.name}")
    private String commentContainerCreatedTopicName;
    @Value("${app.kafka.topic.comment-added.name}")
    private String commentAddedTopicName;
    @Value("${app.kafka.topic.comment-updated.name}")
    private String commentUpdatedTopicName;
    @Value("${app.kafka.topic.comment-deleted.name}")
    private String commentDeletedTopicName;

    @Override
    public CommentContainer createCommentContainer(@Valid CreateCommentContainerCommand command) {
        verifyThereIsNoDuplicates(command.getDomainObjectId(), command.getDomainObjectType());
        CommentContainer commentContainer = new CommentContainer(command.getDomainObjectId(), command.getDomainObjectType());
        template.insert(commentContainer);
        kafkaTemplate.send(commentContainerCreatedTopicName, commentContainer.getId(), new CommentEvent.CommentContainerCreatedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectType(),
                commentContainer.getDomainObjectId())
        );
        return commentContainer;
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
        Comment comment = commentContainer.addComment(command.getContent(), command.getUserId());
        template.insert(comment);
        template.save(commentContainer);
        kafkaTemplate.send(commentAddedTopicName, commentContainer.getId(), new CommentEvent.CommentAddedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectType(),
                commentContainer.getDomainObjectId(),
                null,
                comment.getId(),
                comment.getContent())
        );
        return comment;
    }

    @Override
    public Comment addReply(@Valid AddReplyCommand command) {
        CommentContainer commentContainer = commentContainerQueryService.fetchByCommentId(command.getParentCommentId());
        Comment parentComment = commentContainer.getCommentById(command.getParentCommentId()).get();
        Comment comment = parentComment.addComment(command.getContent(), command.getUserId());
        template.insert(comment);
        template.save(parentComment);
        kafkaTemplate.send(commentAddedTopicName, commentContainer.getId(), new CommentEvent.CommentAddedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectType(),
                commentContainer.getDomainObjectId(),
                parentComment.getId(),
                comment.getId(),
                comment.getContent())
        );
        return comment;
    }

    @Override
    public void updateComment(@Valid UpdateCommentCommand command) {
        CommentContainer commentContainer = commentContainerQueryService.fetchByCommentId(command.getCommentId());
        Comment comment = commentContainer.getCommentById(command.getCommentId()).get();
        comment.setContent(comment.getContent());
        comment.setUserId(comment.getUserId());
        template.save(comment);
        kafkaTemplate.send(commentUpdatedTopicName, commentContainer.getId(), new CommentEvent.CommentUpdatedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectType(),
                commentContainer.getDomainObjectId(),
                comment.getId(),
                comment.getContent())
        );
    }

    @Override
    public void deleteComment(@Valid DeleteCommentCommand command) {
        CommentContainer commentContainer = commentContainerQueryService.fetchByCommentId(command.getCommentId());
        Comment comment = commentContainer.getCommentById(command.getCommentId()).get();
        comment.setDeleteStatus();
        template.save(comment);
        kafkaTemplate.send(commentDeletedTopicName, commentContainer.getId(), new CommentEvent.CommentDeletedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectType(),
                commentContainer.getDomainObjectId(),
                comment.getId())
        );
    }
}
