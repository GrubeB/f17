package pl.app.comment.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

public interface VotingException {
    class NotFoundVotingException extends NotFoundException {
        public NotFoundVotingException() {
            super("not found voting");
        }

        public NotFoundVotingException(String message) {
            super(message);
        }

        public static NotFoundVotingException fromId(String commentId) {
            return new NotFoundVotingException("not found voting with id: " + commentId);
        }

        public static NotFoundVotingException fromDomainObject(String domainObjectId, String domainObjectType) {
            return new NotFoundVotingException("not found voting for domain object id: " + domainObjectId + " of type: " + domainObjectType);
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
            return new DuplicatedDomainObjectException("there is a voting for domain object with id: " + domainObjectId + " of type: " + domainObjectType);
        }
    }
}
