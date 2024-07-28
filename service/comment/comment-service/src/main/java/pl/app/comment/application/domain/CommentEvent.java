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
    class DomainObjectCreatedEvent implements Serializable {
        private String domainObjectType;
        private String domainObjectId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentContainerCreatedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectType;
        private String domainObjectId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentAddedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectType;
        private String domainObjectId;
        private ObjectId parentCommentId;
        private ObjectId commentId;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentUpdatedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectType;
        private String domainObjectId;
        private ObjectId commentId;
        private String newContent;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CommentDeletedEvent implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectType;
        private String domainObjectId;
        private ObjectId commentId;
    }
}
