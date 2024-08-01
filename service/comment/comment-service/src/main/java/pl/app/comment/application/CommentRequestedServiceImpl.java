package pl.app.comment.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.in.CommentRequestedService;

@Component
@RequiredArgsConstructor
class CommentRequestedServiceImpl implements CommentRequestedService {
    private static final Logger logger = LoggerFactory.getLogger(CommentRequestedServiceImpl.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @Value("${app.kafka.topic.create-comment-container-requested.name}")
    private String createCommentContainerRequestedTopicName;
    @Value("${app.kafka.topic.add-comment-requested.name}")
    private String addCommentRequestedTopicName;
    @Value("${app.kafka.topic.update-comment-requested.name}")
    private String updateCommentRequestedTopicName;
    @Value("${app.kafka.topic.delete-comment-requested.name}")
    private String deleteCommentRequestedTopicName;


    @Override
    public ObjectId createCommentContainerRequest(CommentCommand.CreateCommentContainerRequestCommand command) {
        final ObjectId idForNewObject = ObjectId.get();
        final var event = new CommentEvent.CreateCommentContainerRequestedEvent(
                idForNewObject,
                command.getDomainObjectId(),
                command.getDomainObjectType()
        );
        kafkaTemplate.send(createCommentContainerRequestedTopicName, idForNewObject, event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        return idForNewObject;
    }

    @Override
    public ObjectId addCommentRequest(CommentCommand.AddCommentRequestCommand command) {
        final ObjectId idForNewObject = ObjectId.get();
        final var event = new CommentEvent.AddCommentRequestedEvent(
                command.getCommentContainerId(),
                command.getDomainObjectId(),
                command.getDomainObjectType(),
                idForNewObject,
                command.getParentCommentId(),
                command.getUserId(),
                command.getContent()
        );
        kafkaTemplate.send(addCommentRequestedTopicName, idForNewObject, event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
        return idForNewObject;
    }

    @Override
    public void updateCommentRequest(CommentCommand.UpdateCommentRequestCommand command) {
        final var event = new CommentEvent.UpdateCommentRequestedEvent(
                command.getCommentId(),
                command.getContent()
        );
        kafkaTemplate.send(updateCommentRequestedTopicName, command.getCommentId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
    }

    @Override
    public void deleteCommentRequest(CommentCommand.DeleteCommentRequestCommand command) {
        final var event = new CommentEvent.DeleteCommentRequestedEvent(
                command.getCommentId()
        );
        kafkaTemplate.send(deleteCommentRequestedTopicName, command.getCommentId(), event);
        logger.debug("send {} - {}", event.getClass().getSimpleName(), event);

    }
}
