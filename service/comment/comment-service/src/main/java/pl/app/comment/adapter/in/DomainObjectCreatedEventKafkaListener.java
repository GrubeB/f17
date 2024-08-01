package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;
import pl.app.comment.application.domain.CommentException;
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.in.CommentService;


@Component
@RequiredArgsConstructor
class DomainObjectCreatedEventKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(DomainObjectCreatedEventKafkaListener.class);
    private final CommentService commentService;

    @KafkaListener(
            id = "create-comment-container-requested-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.create-comment-container-requested.name}"
    )
    public void consumeCreateCommentContainerRequestedEvent(ConsumerRecord<ObjectId, CommentEvent.CreateCommentContainerRequestedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        try {
            var command = new CommentCommand.CreateCommentContainerCommand(
                    event.getIdForCommentContainerId(),
                    event.getDomainObjectId(),
                    event.getDomainObjectType()
            );
            commentService.createCommentContainer(command);
        } catch (CommentException.DuplicatedDomainObjectException exception) {
            logger.debug("exception occurred while processing event {} {}-{} key: {},value: {}, exception: {}", record.value().getClass().getSimpleName(),
                    record.partition(), record.offset(), record.key(), record.value(), exception.getMessage());
        }
    }

    @KafkaListener(
            id = "add-comment-requested-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.add-comment-requested.name}"
    )
    public void consumeAddCommentRequestCommand(ConsumerRecord<ObjectId, CommentEvent.AddCommentRequestedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CommentCommand.AddCommentCommand(
                event.getCommentContainerId(),
                event.getDomainObjectId(),
                event.getDomainObjectType(),
                event.getIdForCommentId(),
                event.getParentCommentId(),
                event.getUserId(),
                event.getContent()
        );
        commentService.addComment(command);
    }

    @KafkaListener(
            id = "update-comment-requested-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.update-comment-requested.name}"
    )
    public void consumeUpdateCommentRequestedEvent(ConsumerRecord<ObjectId, CommentEvent.UpdateCommentRequestedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CommentCommand.UpdateCommentCommand(
                event.getCommentId(),
                event.getNewContent()
        );
        commentService.updateComment(command);
    }

    @KafkaListener(
            id = "delete-comment-requested-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.delete-comment-requested.name}"
    )
    public void consumeDeleteCommentRequestedEvent(ConsumerRecord<ObjectId, CommentEvent.DeleteCommentRequestedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        var command = new CommentCommand.DeleteCommentCommand(
                event.getCommentId()
        );
        commentService.deleteComment(command);
    }
}
