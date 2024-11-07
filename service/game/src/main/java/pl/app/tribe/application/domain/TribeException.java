package pl.app.tribe.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface TribeException {
    class NotFoundTribeException extends NotFoundException {
        public NotFoundTribeException() {
            super("not found tribe");
        }

        public NotFoundTribeException(String message) {
            super(message);
        }

        public static NotFoundTribeException fromId(String id) {
            return new NotFoundTribeException("not found tribe: " + id);
        }
    }

    class DuplicatedNameException extends ValidationException {
        public DuplicatedNameException() {
            super("there is tribe with given name");
        }

        public DuplicatedNameException(String message) {
            super(message);
        }

        public static DuplicatedNameException fromName(String id) {
            return new DuplicatedNameException(MessageFormat.format("there is tribe with given name: {0}", id));
        }
    }
}
