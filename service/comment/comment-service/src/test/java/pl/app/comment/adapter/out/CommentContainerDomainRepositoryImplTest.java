package pl.app.comment.adapter.out;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import pl.app.AbstractIntegrationTest;
import pl.app.comment.application.domain.Comment;
import pl.app.comment.application.domain.CommentContainer;
import pl.app.comment.application.domain.CommentException;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(CommentContainerDomainRepositoryImpl.class)
class CommentContainerDomainRepositoryImplTest extends AbstractIntegrationTest {
    @Autowired
    private CommentContainerDomainRepositoryImpl repository;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void fetchById_shouldReturnOne_whenExists() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";
        final var userId = UUID.randomUUID().toString();

        CommentContainer commentContainer = new CommentContainer(commentContainerId, domainObjectId, domainObjectType);
        Comment comment = commentContainer.addComment(commentId, "content", userId);
        mongoTemplate.save(commentContainer).block();
        mongoTemplate.save(comment).block();

        StepVerifier.create(repository.fetchById(commentContainerId))
                .assertNext(next -> {
                    assertThat(next.getComments()).isNotNull();
                    assertThat(next.getComments()).hasSize(1);
                })
                .verifyComplete();
    }

    @Test
    void fetchById_shouldThrowNotFoundException_whenContainerNotExist() {
        StepVerifier.create(repository.fetchById(ObjectId.get()))
                .expectError(CommentException.NotFoundCommentContainerException.class)
                .verify();
    }

    @Test
    void fetchByCommentId_shouldReturnOne_whenCommentExists() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";
        final var userId = UUID.randomUUID().toString();

        CommentContainer commentContainer = new CommentContainer(commentContainerId, domainObjectId, domainObjectType);
        Comment comment = commentContainer.addComment(commentId, "content", userId);
        mongoTemplate.save(commentContainer).block();
        mongoTemplate.save(comment).block();

        StepVerifier.create(repository.fetchByCommentId(commentId))
                .assertNext(next -> {
                    assertThat(next.getComments()).isNotNull();
                    assertThat(next.getComments()).hasSize(1);
                    assertThat(next.getComments().iterator().next().getId()).isEqualTo(commentId);
                })
                .verifyComplete();
    }

    @Test
    void fetchByCommentId_shouldThrowNotFoundException_whenCommentNotExist() {
        StepVerifier.create(repository.fetchByCommentId(ObjectId.get()))
                .expectError(CommentException.NotFoundCommentException.class)
                .verify();
    }

    @Test
    void fetchByDomainObject_shouldReturnOne_whenExists() {
        final var commentContainerId = ObjectId.get();
        final var commentId = ObjectId.get();
        final var domainObjectId = UUID.randomUUID().toString();
        final var domainObjectType = "TEST_TYPE";
        final var userId = UUID.randomUUID().toString();

        CommentContainer commentContainer = new CommentContainer(commentContainerId, domainObjectId, domainObjectType);
        Comment comment = commentContainer.addComment(commentId, "content", userId);
        mongoTemplate.save(commentContainer).block();
        mongoTemplate.save(comment).block();

        StepVerifier.create(repository.fetchByDomainObject(domainObjectId, domainObjectType))
                .assertNext(next -> {
                    assertThat(next.getComments()).isNotNull();
                    assertThat(next.getComments()).hasSize(1);
                })
                .verifyComplete();
    }

}