package pl.app.comment.application;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import pl.app.AbstractIntegrationTest;
import pl.app.comment.application.domain.*;
import pl.app.comment.application.port.in.CommentCommand;
import pl.app.comment.application.port.out.CommentContainerDomainRepository;
import pl.app.config.KafkaTopicConfigurationProperties;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private CommentServiceImpl commentService;
    @SpyBean
    private CommentContainerDomainRepository domainRepository;
    @SpyBean
    private ReactiveMongoTemplate mongoTemplate;
    @SpyBean
    private KafkaTemplate<ObjectId, Object> kafkaTemplate;
    @SpyBean
    private KafkaTopicConfigurationProperties topicNames;

    @Test
    void contextLoads() {
        Assertions.assertThat(commentService).isNotNull();
        Assertions.assertThat(domainRepository).isNotNull();
        Assertions.assertThat(mongoTemplate).isNotNull();
        Assertions.assertThat(kafkaTemplate).isNotNull();
        Assertions.assertThat(topicNames).isNotNull();
    }

    @Test
    void createCommentContainer_shouldCreateCommentContainer_whenCommandIsValid() {
        final var commentContainerId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.CreateCommentContainerCommand(
                commentContainerId,
                domainObjectId,
                domainObjectType
        );

        StepVerifier.create(commentService.createCommentContainer(command))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getId()).isEqualTo(commentContainerId);
                    Assertions.assertThat(next.getDomainObjectId()).isEqualTo(domainObjectId);
                    Assertions.assertThat(next.getDomainObjectType()).isEqualTo(domainObjectType);
                }).verifyComplete();
        verify(mongoTemplate, times(1)).insert(any(CommentContainer.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCommentContainerCreated().getName()), eq(commentContainerId), any(CommentEvent.CommentContainerCreatedEvent.class));
    }

    @Test
    void createCommentContainer_shouldThrowException_whenCommandContainerDuplicated() {
        final var commentContainerId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.CreateCommentContainerCommand(
                commentContainerId,
                domainObjectId,
                domainObjectType
        );
        commentService.createCommentContainer(command).block();
        StepVerifier.create(commentService.createCommentContainer(command))
                .expectError(CommentException.DuplicatedDomainObjectException.class)
                .verify();
    }

    @Test
    void addComment_shouldAddCommentToContainerByCommentContainerId_whenCommandIsValidAndContainerExist() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var userId = UUID.randomUUID().toString();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.AddCommentCommand(
                commentContainerId,
                null,
                null,
                commentId,
                null,
                userId,
                "content"
        );
        commentService.createCommentContainer(new CommentCommand.CreateCommentContainerCommand(
                commentContainerId, domainObjectId, domainObjectType
        )).block();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        StepVerifier.create(commentService.addComment(command))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getId()).isEqualTo(commentContainerId);
                    Assertions.assertThat(next.getDomainObjectId()).isEqualTo(domainObjectId);
                    Assertions.assertThat(next.getDomainObjectType()).isEqualTo(domainObjectType);
                    Assertions.assertThat(next.getCommentById(commentId)).isPresent();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(CommentContainer.class));
        verify(mongoTemplate, times(1)).insert(any(Comment.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCommentAdded().getName()), eq(commentContainerId), any(CommentEvent.CommentAddedEvent.class));
    }

    @Test
    void addComment_shouldAddCommentToContainerByDomainObject_whenCommandIsValidAndContainerExist() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var userId = UUID.randomUUID().toString();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.AddCommentCommand(
                null,
                domainObjectId,
                domainObjectType,
                commentId,
                null,
                userId,
                "content"
        );
        commentService.createCommentContainer(new CommentCommand.CreateCommentContainerCommand(
                commentContainerId, domainObjectId, domainObjectType
        )).block();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        StepVerifier.create(commentService.addComment(command))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getId()).isEqualTo(commentContainerId);
                    Assertions.assertThat(next.getDomainObjectId()).isEqualTo(domainObjectId);
                    Assertions.assertThat(next.getDomainObjectType()).isEqualTo(domainObjectType);
                    Assertions.assertThat(next.getCommentById(commentId)).isPresent();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(CommentContainer.class));
        verify(mongoTemplate, times(1)).insert(any(Comment.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCommentAdded().getName()), eq(commentContainerId), any(CommentEvent.CommentAddedEvent.class));
    }

    @Test
    void addComment_shouldAddCommentToContainerByParentCommentId_whenCommandIsValidAndContainerExist() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var parentCommentId = ObjectId.get();
        final var userId = UUID.randomUUID().toString();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.AddCommentCommand(
                null,
                null,
                null,
                commentId,
                parentCommentId,
                userId,
                "content2"
        );
        commentService.createCommentContainer(new CommentCommand.CreateCommentContainerCommand(
                commentContainerId, domainObjectId, domainObjectType
        )).block();
        commentService.addComment(new CommentCommand.AddCommentCommand(
                commentContainerId, null, null, parentCommentId, null, userId, "content"
        )).block();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        StepVerifier.create(commentService.addComment(command))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getId()).isEqualTo(commentContainerId);
                    Assertions.assertThat(next.getDomainObjectId()).isEqualTo(domainObjectId);
                    Assertions.assertThat(next.getDomainObjectType()).isEqualTo(domainObjectType);
                    Assertions.assertThat(next.getCommentById(commentId)).isPresent();
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(CommentContainer.class));
        verify(mongoTemplate, times(1)).save(any(Comment.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCommentAdded().getName()), eq(commentContainerId), any(CommentEvent.CommentAddedEvent.class));
    }

    @Test
    void addComment_shouldThrowError_whenCommandIsInvalid() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var userId = UUID.randomUUID().toString();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.AddCommentCommand(
                null,
                null,
                null,
                commentId,
                null,
                userId,
                "content"
        );
        commentService.createCommentContainer(new CommentCommand.CreateCommentContainerCommand(
                commentContainerId, domainObjectId, domainObjectType
        )).block();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        StepVerifier.create(commentService.addComment(command))
                .expectError(CommentException.NotFoundCommentContainerException.class)
                .verify();
    }

    @Test
    void updateComment_shouldUpdateComment_whenCommentExists() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var userId = UUID.randomUUID().toString();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.UpdateCommentCommand(
                commentId, "updated_content"
        );
        commentService.createCommentContainer(new CommentCommand.CreateCommentContainerCommand(
                commentContainerId, domainObjectId, domainObjectType
        )).block();
        commentService.addComment(new CommentCommand.AddCommentCommand(
                commentContainerId, null, null, commentId, null, userId, "content"
        )).block();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        StepVerifier.create(commentService.updateComment(command))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getCommentById(commentId)).isPresent();
                    next.getCommentById(commentId).ifPresent(comment -> {
                        Assertions.assertThat(comment.getId()).isEqualTo(commentId);
                        Assertions.assertThat(comment.getContent()).isEqualTo("updated_content");
                    });
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Comment.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCommentUpdated().getName()), eq(commentContainerId), any(CommentEvent.CommentUpdatedEvent.class));
    }

    @Test
    void deleteComment_shouldDeleteComment_whenCommentExists() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var userId = UUID.randomUUID().toString();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";

        final var command = new CommentCommand.DeleteCommentCommand(
                commentId
        );
        commentService.createCommentContainer(new CommentCommand.CreateCommentContainerCommand(
                commentContainerId, domainObjectId, domainObjectType
        )).block();
        commentService.addComment(new CommentCommand.AddCommentCommand(
                commentContainerId, null, null, commentId, null, userId, "content"
        )).block();
        Mockito.reset(domainRepository, mongoTemplate, kafkaTemplate, topicNames);

        StepVerifier.create(commentService.deleteComment(command))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getCommentById(commentId)).isPresent();
                    next.getCommentById(commentId).ifPresent(comment -> {
                        Assertions.assertThat(comment.getId()).isEqualTo(commentId);
                        Assertions.assertThat(comment.getStatus()).isEqualTo(CommentStatus.DELETED);
                    });
                }).verifyComplete();
        verify(mongoTemplate, times(1)).save(any(Comment.class));
        verify(kafkaTemplate, times(1))
                .send(eq(topicNames.getCommentDeleted().getName()), eq(commentContainerId), any(CommentEvent.CommentDeletedEvent.class));
    }
}