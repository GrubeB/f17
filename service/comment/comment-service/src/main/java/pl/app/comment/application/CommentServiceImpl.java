package pl.app.comment.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.in.CommentService;
import pl.app.comment.application.port.out.CommentContainerDomainRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final MongoTemplate template;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final CommentContainerDomainRepository domainRepository;

    @Value("${app.kafka.topic.comment-container-created.name}")
    private String commentContainerCreatedTopicName;
    @Value("${app.kafka.topic.comment-added.name}")
    private String commentAddedTopicName;
    @Value("${app.kafka.topic.comment-updated.name}")
    private String commentUpdatedTopicName;
    @Value("${app.kafka.topic.comment-deleted.name}")
    private String commentDeletedTopicName;

    @Override
    public CommentContainer createCommentContainer(@Valid CommentCommand.CreateCommentContainerCommand command) {
        verifyThereIsNoDuplicates(command.getDomainObjectId(), command.getDomainObjectType());
        CommentContainer commentContainer = new CommentContainer(command.getIdForCommentContainerId(), command.getDomainObjectId(), command.getDomainObjectType());
        template.insert(commentContainer);
        var event = new CommentEvent.CommentContainerCreatedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectId(),
                commentContainer.getDomainObjectType());
        kafkaTemplate.send(commentContainerCreatedTopicName, commentContainer.getId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        logger.debug("created comment container with id: " + commentContainer.getId() + ", for domain object: " + commentContainer.getDomainObjectId() + " of type:" + commentContainer.getDomainObjectType());
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
    public Comment addComment(@Valid CommentCommand.AddCommentCommand command) {
        CommentContainer commentContainer;
        Comment comment;
        if (Objects.nonNull(command.getParentCommentId())) {
            commentContainer = domainRepository.fetchByCommentId(command.getParentCommentId());
            Comment parentComment = commentContainer.getCommentById(command.getParentCommentId()).get();
            comment = parentComment.addComment(command.getIdForCommentContainerId(), command.getContent(), command.getUserId());
            template.save(parentComment);
        } else {
            commentContainer = domainRepository.fetchByIdOrDomainObject(command.getCommentContainerId(), command.getDomainObjectId(), command.getDomainObjectType());
            comment = commentContainer.addComment(command.getIdForCommentContainerId(), command.getContent(), command.getUserId());
        }
        template.insert(comment);
        template.save(commentContainer);
        var event = new CommentEvent.CommentAddedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectId(),
                commentContainer.getDomainObjectType(),
                Objects.nonNull(comment.getParentComment()) ? comment.getParentComment().getId() : null,
                comment.getId(),
                comment.getContent());
        kafkaTemplate.send(commentAddedTopicName, commentContainer.getId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        logger.debug("created comment " + comment.getId() + " for comment container with id: " + commentContainer.getId());
        return comment;
    }

    @Override
    public void updateComment(@Valid CommentCommand.UpdateCommentCommand command) {
        CommentContainer commentContainer = domainRepository.fetchByCommentId(command.getCommentId());
        Comment comment = commentContainer.getCommentById(command.getCommentId()).get();
        comment.setContent(command.getContent());
        template.save(comment);
        var event = new CommentEvent.CommentUpdatedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectId(),
                commentContainer.getDomainObjectType(),
                comment.getId(),
                comment.getContent());
        kafkaTemplate.send(commentUpdatedTopicName, commentContainer.getId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        logger.debug("updated comment " + comment.getId() + " for comment container with id: " + commentContainer.getId());
    }

    @Override
    public void deleteComment(@Valid CommentCommand.DeleteCommentCommand command) {
        CommentContainer commentContainer = domainRepository.fetchByCommentId(command.getCommentId());
        Comment comment = commentContainer.getCommentById(command.getCommentId()).get();
        comment.setDeleteStatus();
        template.save(comment);
        var event = new CommentEvent.CommentDeletedEvent(
                commentContainer.getId(),
                commentContainer.getDomainObjectId(),
                commentContainer.getDomainObjectType(),
                comment.getId());
        kafkaTemplate.send(commentDeletedTopicName, commentContainer.getId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        logger.debug("deleted comment " + comment.getId() + " for comment container with id: " + commentContainer.getId());
    }
}
