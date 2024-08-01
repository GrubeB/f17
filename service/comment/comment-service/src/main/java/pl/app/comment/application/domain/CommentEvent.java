package pl.app.comment.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface CommentEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentContainerCreatedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectId;
        private String domainObjectType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentAddedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectId;
        private String domainObjectType;

        private ObjectId parentCommentId;
        private ObjectId commentId;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentUpdatedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectId;
        private String domainObjectType;

        private ObjectId commentId;
        private String newContent;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentDeletedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectId;
        private String domainObjectType;

        private ObjectId commentId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateCommentContainerRequestedEvent implements Serializable {
        private ObjectId idForCommentContainerId;
        private String domainObjectId;
        private String domainObjectType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class AddCommentRequestedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectId;
        private String domainObjectType;

        private ObjectId idForCommentId;
        private ObjectId parentCommentId;
        private String userId;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateCommentRequestedEvent implements Serializable {
        private ObjectId commentId;
        private String newContent;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteCommentRequestedEvent implements Serializable {
        private ObjectId commentId;
    }

}
