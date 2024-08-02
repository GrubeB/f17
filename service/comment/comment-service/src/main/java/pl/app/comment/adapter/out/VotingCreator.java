package pl.app.comment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;
import pl.app.common.shared.DomainObjectTyp;
import pl.app.voting.application.domain.VotingEvent;


@Component
@RequiredArgsConstructor
class VotingCreator {
    private static final Logger logger = LoggerFactory.getLogger(VotingCreator.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;

    @Value("${app.kafka.topic.create-voting-requested.name}")
    private String createVotingRequestedTopicName;

    @KafkaListener(
            id = "comment-added-event-listener",
            groupId = "${app.kafka.consumer.group-id}",
            topics = "${app.kafka.topic.comment-added.name}"
    )
    public void consumeCreateCommentContainerRequestedEvent(ConsumerRecord<ObjectId, CommentEvent.CommentAddedEvent> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        final var createVotingRequestedEvent = new VotingEvent.CreateVotingRequestedEvent(
                ObjectId.get(),
                event.getCommentId().toString(),
                DomainObjectTyp.COMMENT.toString()
        );
        kafkaTemplate.send(createVotingRequestedTopicName, createVotingRequestedEvent.getVotingId(), createVotingRequestedEvent);
        logger.debug("send {} - {}", createVotingRequestedEvent.getClass().getSimpleName(), createVotingRequestedEvent);
    }
}
