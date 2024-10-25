package pl.app.unit.recruiter.application.domain;

import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface RecruiterException {
    class NotFoundRecruiterException extends NotFoundException {
        public NotFoundRecruiterException() {
            super("not found recruiter");
        }

        public NotFoundRecruiterException(String message) {
            super(message);
        }

        public static NotFoundRecruiterException fromId(String id) {
            return new NotFoundRecruiterException("not found village recruiter: " + id);
        }
    }

    class DuplicatedRecruiterException extends ValidationException {
        public DuplicatedRecruiterException() {
            super("there are recruiter for given village");
        }

        public DuplicatedRecruiterException(String message) {
            super(message);
        }

        public static DuplicatedRecruiterException fromId(String id) {
            return new DuplicatedRecruiterException(MessageFormat.format("there are recruiter for given village: {0}", id));
        }
    }

    class ReachedMaxRecruitRequestNumberException extends InvalidStateException {
        public ReachedMaxRecruitRequestNumberException() {
            super("reached max recruit request number");
        }

        public ReachedMaxRecruitRequestNumberException(String message) {
            super(message);
        }
    }

    class VillageDoseNotMeetRequirementsException extends InvalidStateException {
        public VillageDoseNotMeetRequirementsException() {
            super("village does not meet requirements");
        }

        public VillageDoseNotMeetRequirementsException(String message) {
            super(message);
        }
    }
}
