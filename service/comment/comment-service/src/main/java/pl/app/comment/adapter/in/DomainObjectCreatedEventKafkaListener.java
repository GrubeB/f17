package pl.app.comment.adapter.in;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;
import pl.app.comment.application.port.in.CommentService;
import pl.app.comment.application.port.in.command.CreateCommentContainerCommand;

@Component
@RequiredArgsConstructor
class DomainObjectCreatedEventKafkaListener {
    private final Logger logger = LoggerFactory.getLogger(DomainObjectCreatedEventKafkaListener.class);
    private final CommentService commentService;

    // TODO throw error when event is not instance of DomainObjectCreatedEvent.class
    @KafkaListener(
            id = "domain-object-created-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.domain-object-created.name}"
    )
    public void consume(ConsumerRecord<ObjectId, Object> record) {
        logger.debug("received " + record.partition() + ":" + record.offset() + " - " + record.key() + " with value: " + record.value());
        if (record.value() instanceof CommentEvent.DomainObjectCreatedEvent event) {
            CreateCommentContainerCommand command = new CreateCommentContainerCommand(event.getDomainObjectId(), event.getDomainObjectType());
            commentService.createCommentContainer(command);
        }
    }
}
