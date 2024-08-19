package pl.app.comment.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentContainerDomainRepository domainRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<CommentContainer> createCommentContainer(@Valid CommentCommand.CreateCommentContainerCommand command) {
        logger.debug("creating comment container: {}, for domain object: {} of type: {}", command.getIdForNewObject(), command.getDomainObjectId(), command.getDomainObjectType());
        return Mono.when(verifyThereIsNoDuplicates(command.getDomainObjectId(), command.getDomainObjectType()))
                .doOnError(e -> logger.error("exception occurred while creating comment container: {}, exception: {}", command.getIdForNewObject(), e.getMessage()))
                .then(Mono.defer(() -> {
                    CommentContainer commentContainer = new CommentContainer(command.getIdForNewObject(), command.getDomainObjectId(), command.getDomainObjectType());
                    var event = new CommentEvent.CommentContainerCreatedEvent(
                            commentContainer.getId(),
                            commentContainer.getDomainObjectId(),
                            commentContainer.getDomainObjectType());
                    return mongoTemplate.insert(commentContainer)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCommentContainerCreated().getName(), saved.getId(), event)).thenReturn(saved))
                            .doOnSuccess(saved -> {
                                logger.debug("created comment container: {}, for domain object: {} of type: {}", saved.getId(), saved.getDomainObjectId(), saved.getDomainObjectType());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            });
                }));
    }

    private Mono<Void> verifyThereIsNoDuplicates(String domainObjectId, String domainObjectType) {
        Query query = Query.query(Criteria
                .where("domainObjectId").is(domainObjectId)
                .and("domainObjectType").is(domainObjectType)
        );
        return mongoTemplate.exists(query, CommentContainer.class)
                .flatMap(exist -> exist ? Mono.error(CommentException.DuplicatedDomainObjectException.fromDomainObject(domainObjectId, domainObjectType)) : Mono.empty());
    }

    @Override
    public Mono<CommentContainer> addComment(@Valid CommentCommand.AddCommentCommand command) {
        if (Objects.nonNull(command.getParentCommentId())) {
            return addReplay(command);
        } else {
            return addCommentInternal(command);
        }
    }

    private Mono<CommentContainer> addCommentInternal(@Valid CommentCommand.AddCommentCommand command) {
        logger.debug("adding comment to container: {}, for domain object: {} of type: {}", command.getCommentContainerId(), command.getDomainObjectId(), command.getDomainObjectType());
        return domainRepository.fetchByIdOrDomainObject(command.getCommentContainerId(), command.getDomainObjectId(), command.getDomainObjectType())
                .doOnError(e -> logger.error("exception occurred while adding comment to container: {}, for domain object: {} of type: {}, exception: {}", command.getCommentContainerId(), command.getDomainObjectId(), command.getDomainObjectType(), e.getMessage()))
                .flatMap(commentContainer -> {
                    Comment comment = commentContainer.addComment(command.getIdForNewObject(), command.getContent(), command.getUserId());
                    var event = new CommentEvent.CommentAddedEvent(
                            commentContainer.getId(),
                            commentContainer.getDomainObjectId(),
                            commentContainer.getDomainObjectType(),
                            Objects.nonNull(comment.getParentComment()) ? comment.getParentComment().getId() : null,
                            comment.getId(),
                            comment.getContent());

                    return mongoTemplate.insert(comment)
                            .flatMap(unused -> mongoTemplate.save(commentContainer))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCommentAdded().getName(), commentContainer.getId(), event)).then())
                            .doOnSuccess(unused -> {
                                logger.debug("added comment:{}, to container: {}, for domain object: {} of type: {}", comment.getId(), commentContainer.getId(), commentContainer.getDomainObjectId(), commentContainer.getDomainObjectType());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(commentContainer);
                });
    }

    private Mono<CommentContainer> addReplay(@Valid CommentCommand.AddCommentCommand command) {
        logger.debug("adding comment to comment: {}", command.getParentCommentId());
        return domainRepository.fetchByCommentId(command.getParentCommentId())
                .doOnError(e -> logger.error("exception occurred while adding comment to comment: {}, exception: {}", command.getParentCommentId(), e.getMessage()))
                .flatMap(commentContainer -> {
                    Comment parentComment = commentContainer.getCommentById(command.getParentCommentId()).get();
                    Comment comment = parentComment.addComment(command.getIdForNewObject(), command.getContent(), command.getUserId());
                    var event = new CommentEvent.CommentAddedEvent(
                            commentContainer.getId(),
                            commentContainer.getDomainObjectId(),
                            commentContainer.getDomainObjectType(),
                            Objects.nonNull(comment.getParentComment()) ? comment.getParentComment().getId() : null,
                            comment.getId(),
                            comment.getContent());

                    return mongoTemplate.insert(comment)
                            .flatMap(unused -> mongoTemplate.save(parentComment))
                            .flatMap(unused -> mongoTemplate.save(commentContainer))
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCommentAdded().getName(), commentContainer.getId(), event)).then())
                            .doOnSuccess(unused -> {
                                logger.debug("added comment: {}, to container: {}, for domain object: {} of type: {}", comment.getId(), commentContainer.getId(), commentContainer.getDomainObjectId(), commentContainer.getDomainObjectType());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(commentContainer);
                });
    }

    @Override
    public Mono<CommentContainer> updateComment(@Valid CommentCommand.UpdateCommentCommand command) {
        logger.debug("updating comment: {}", command.getCommentId());
        return domainRepository.fetchByCommentId(command.getCommentId())
                .doOnError(e -> logger.error("exception occurred while updating comment: {}, exception: {}", command.getCommentId(), e.getMessage()))
                .flatMap(commentContainer -> {
                    Comment comment = commentContainer.getCommentById(command.getCommentId()).get();
                    comment.setContent(command.getContent());
                    var event = new CommentEvent.CommentUpdatedEvent(
                            commentContainer.getId(),
                            commentContainer.getDomainObjectId(),
                            commentContainer.getDomainObjectType(),
                            comment.getId(),
                            comment.getContent());

                    return mongoTemplate.save(comment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCommentUpdated().getName(), commentContainer.getId(), event)).then())
                            .doOnSuccess(unused -> {
                                logger.debug("updated comment:{}, from container: {}, for domain object: {} of type: {}", comment.getId(), commentContainer.getId(), commentContainer.getDomainObjectId(), commentContainer.getDomainObjectType());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(commentContainer);
                });
    }

    @Override
    public Mono<CommentContainer> deleteComment(@Valid CommentCommand.DeleteCommentCommand command) {
        logger.debug("deleting comment: {}", command.getCommentId());
        return domainRepository.fetchByCommentId(command.getCommentId())
                .doOnError(e -> logger.error("exception occurred while deleting comment: {}, exception: {}", command.getCommentId(), e.getMessage()))
                .flatMap(commentContainer -> {
                    Comment comment = commentContainer.getCommentById(command.getCommentId()).get();
                    comment.setDeleteStatus();
                    var event = new CommentEvent.CommentDeletedEvent(
                            commentContainer.getId(),
                            commentContainer.getDomainObjectId(),
                            commentContainer.getDomainObjectType(),
                            comment.getId());

                    return mongoTemplate.save(comment)
                            .flatMap(saved -> Mono.fromFuture(kafkaTemplate.send(topicNames.getCommentDeleted().getName(), commentContainer.getId(), event)).then())
                            .doOnSuccess(unused -> {
                                logger.debug("deleted comment:{}, from container: {}, for domain object: {} of type: {}", comment.getId(), commentContainer.getId(), commentContainer.getDomainObjectId(), commentContainer.getDomainObjectType());
                                logger.debug("send {} - {}", event.getClass().getSimpleName(), event);
                            }).thenReturn(commentContainer);
                });
    }
}
