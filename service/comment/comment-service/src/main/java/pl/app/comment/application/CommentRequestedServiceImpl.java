package pl.app.comment.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.comment.application.domain.CommentEvent;
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.in.CommentRequestedService;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CommentRequestedServiceImpl implements CommentRequestedService {
    private static final Logger logger = LoggerFactory.getLogger(CommentRequestedServiceImpl.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<ObjectId> createCommentContainerRequest(CommentCommand.CreateCommentContainerRequestCommand command) {
        final ObjectId idForNewObject = ObjectId.get();
        final var event = new CommentEvent.CreateCommentContainerRequestedEvent(
                idForNewObject,
                command.getDomainObjectId(),
                command.getDomainObjectType()
        );
        return Mono.fromFuture(kafkaTemplate.send(topicNames.getCreateCommentContainerRequested().getName(), idForNewObject, event))
                .doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .thenReturn(idForNewObject);
    }

    @Override
    public Mono<ObjectId> addCommentRequest(CommentCommand.AddCommentRequestCommand command) {
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
        return Mono.fromFuture(kafkaTemplate.send(topicNames.getAddCommentRequested().getName(), idForNewObject, event))
                .doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .thenReturn(idForNewObject);
    }

    @Override
    public Mono<Void> updateCommentRequest(CommentCommand.UpdateCommentRequestCommand command) {
        final var event = new CommentEvent.UpdateCommentRequestedEvent(
                command.getCommentId(),
                command.getContent()
        );
        return Mono.fromFuture(kafkaTemplate.send(topicNames.getUpdateCommentRequested().getName(), command.getCommentId(), event))
                .doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .then();
    }

    @Override
    public Mono<Void> deleteCommentRequest(CommentCommand.DeleteCommentRequestCommand command) {
        final var event = new CommentEvent.DeleteCommentRequestedEvent(
                command.getCommentId()
        );
        return Mono.fromFuture(kafkaTemplate.send(topicNames.getDeleteCommentRequested().getName(), command.getCommentId(), event))
                .doOnSuccess(unused -> logger.debug("send {} - {}", event.getClass().getSimpleName(), event))
                .then();
    }
}
