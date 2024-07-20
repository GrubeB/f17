package pl.app.comment.application.domain;

import pl.app.common.shared.exception.NotFoundException;

public interface CommentException {
    class NotFoundCommentException extends NotFoundException {
        public NotFoundCommentException() {
            super("not found comment");
        }

        public NotFoundCommentException(String message) {
            super(message);
        }

        public static NotFoundCommentException fromId(String commentId) {
            return new NotFoundCommentException("not found comment with id: " + commentId);
        }
    }
    class NotFoundCommentContainerException extends NotFoundException {
        public NotFoundCommentContainerException() {
            super("not found comment container");
        }

        public NotFoundCommentContainerException(String message) {
            super(message);
        }

        public static NotFoundCommentContainerException fromId(String commentId) {
            return new NotFoundCommentContainerException("not found comment container with id: " + commentId);
        }
        public static NotFoundCommentContainerException fromDomainObject(String domainObjectId, String domainObjectType) {
            return new NotFoundCommentContainerException("not found comment container for domain object id: " + domainObjectId + " of type: " + domainObjectType);
        }
    }
}
