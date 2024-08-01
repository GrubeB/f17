package pl.app.comment.application.port.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface CommentCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateCommentContainerCommand implements Serializable {
        private ObjectId idForCommentContainerId;
        @NotBlank
        private String domainObjectId;
        @NotBlank
        private String domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddCommentCommand implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectId;
        private String domainObjectType;

        private ObjectId idForCommentContainerId;
        private ObjectId parentCommentId;
        private String userId;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateCommentCommand implements Serializable {
        private ObjectId commentId;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteCommentCommand implements Serializable {
        private ObjectId commentId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateCommentContainerRequestCommand implements Serializable {
        @NotBlank
        private String domainObjectId;
        @NotBlank
        private String domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddCommentRequestCommand implements Serializable {
        private ObjectId commentContainerId;
        private String domainObjectId;
        private String domainObjectType;

        private ObjectId parentCommentId;
        private String userId;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateCommentRequestCommand implements Serializable {
        private ObjectId commentId;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteCommentRequestCommand implements Serializable {
        private ObjectId commentId;
    }
}
