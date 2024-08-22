package pl.app.god.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GodException {
    class NotFoundAccountException extends NotFoundException {
        public NotFoundAccountException() {
            super("not found god");
        }

        public NotFoundAccountException(String message) {
            super(message);
        }

        public static NotFoundAccountException fromId(String id) {
            return new NotFoundAccountException("not found god with id: " + id);
        }
    }

    class DuplicatedNameException extends ValidationException {
        public DuplicatedNameException() {
            super("there is a god for given name");
        }

        public DuplicatedNameException(String message) {
            super(message);
        }

        public static DuplicatedNameException fromName(String name) {
            return new DuplicatedNameException(MessageFormat.format("there is a god for given name: {0}", name));
        }
    }
    class InsufficientMoneyException extends InvalidStateException {
        public InsufficientMoneyException() {
            super("insufficient money");
        }

        public InsufficientMoneyException(String message) {
            super(message);
        }
    }
    class InvalidAmountException extends IllegalArgumentException {
        public InvalidAmountException() {
            super("invalid amount");
        }

        public InvalidAmountException(String message) {
            super(message);
        }
    }
}
