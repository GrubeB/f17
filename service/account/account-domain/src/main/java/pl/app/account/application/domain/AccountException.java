package pl.app.account.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface AccountException {
    class NotFoundAccountException extends NotFoundException {
        public NotFoundAccountException() {
            super("not found account");
        }

        public NotFoundAccountException(String message) {
            super(message);
        }

        public static NotFoundAccountException fromId(String id) {
            return new NotFoundAccountException("not found account with id: " + id);
        }
    }

    class DuplicatedNameException extends ValidationException {
        public DuplicatedNameException() {
            super("there is a account for given name");
        }

        public DuplicatedNameException(String message) {
            super(message);
        }

        public static DuplicatedNameException fromName(String name) {
            return new DuplicatedNameException(MessageFormat.format("there is a account for given name: {0}", name));
        }
    }
}
