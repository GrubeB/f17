package pl.app.comment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;
import pl.app.common.shared.DomainObjectTyp;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.voting.application.domain.VotingEvent;


@Component
@RequiredArgsConstructor
class VotingCreator {
    private static final Logger logger = LoggerFactory.getLogger(VotingCreator.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;

    private final KafkaTopicConfigurationProperties topicNames;

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
        kafkaTemplate.send(topicNames.getCreateVotingRequested().getName(), createVotingRequestedEvent.getVotingId(), createVotingRequestedEvent);
        logger.debug("send {} - {}", createVotingRequestedEvent.getClass().getSimpleName(), createVotingRequestedEvent);
    }
}
