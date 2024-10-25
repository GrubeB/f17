package pl.app.building.builder.application.domain;

import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface BuilderException {
    class NotFoundBuilderException extends NotFoundException {
        public NotFoundBuilderException() {
            super("not found village builder");
        }

        public NotFoundBuilderException(String message) {
            super(message);
        }

        public static NotFoundBuilderException fromId(String id) {
            return new NotFoundBuilderException("not found village builder: " + id);
        }
    }

    class DuplicatedBuilderException extends ValidationException {
        public DuplicatedBuilderException() {
            super("there are builder for given village");
        }

        public DuplicatedBuilderException(String message) {
            super(message);
        }

        public static DuplicatedBuilderException fromId(String id) {
            return new DuplicatedBuilderException(MessageFormat.format("there are builder for given village: {0}", id));
        }
    }

    class ReachedMaxConstructorNumberException extends InvalidStateException {
        public ReachedMaxConstructorNumberException() {
            super("reached max constructor number");
        }

        public ReachedMaxConstructorNumberException(String message) {
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
