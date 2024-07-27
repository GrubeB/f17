package pl.app.comment.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

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

    class DuplicatedDomainObjectException extends ValidationException {
        public DuplicatedDomainObjectException() {
            super("there is a container for given domain object");
        }

        public DuplicatedDomainObjectException(String message) {
            super(message);
        }

        public static DuplicatedDomainObjectException fromDomainObject(String domainObjectId, String domainObjectType) {
            return new DuplicatedDomainObjectException("there is a container for domain object with id: " + domainObjectId + " of type: " + domainObjectType);
        }
    }
}
