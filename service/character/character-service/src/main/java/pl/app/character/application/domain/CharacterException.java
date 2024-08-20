package pl.app.character.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface CharacterException {
    class NotFoundCharacterException extends NotFoundException {
        public NotFoundCharacterException() {
            super("not found character");
        }

        public NotFoundCharacterException(String message) {
            super(message);
        }

        public static NotFoundCharacterException fromId(String id) {
            return new NotFoundCharacterException("not found character with id: " + id);
        }
    }

    class DuplicatedNameException extends ValidationException {
        public DuplicatedNameException() {
            super("there is a character for given name");
        }

        public DuplicatedNameException(String message) {
            super(message);
        }

        public static DuplicatedNameException fromName(String name) {
            return new DuplicatedNameException(MessageFormat.format("there is a character for given name: {0}", name));
        }
    }

    class NoSuchStatisticException extends IllegalArgumentException {
        public NoSuchStatisticException() {
            super("there is not statistic");
        }

        public NoSuchStatisticException(String message) {
            super(message);
        }

        public static NoSuchStatisticException fromName(String name) {
            return new NoSuchStatisticException(MessageFormat.format("there is not statistic of name: {0}", name));
        }
    }

    class UnmodifiableStatisticException extends IllegalArgumentException {
        public UnmodifiableStatisticException() {
            super("statistic is unmodifiable");
        }

        public UnmodifiableStatisticException(String message) {
            super(message);
        }

        public static UnmodifiableStatisticException fromName(String name) {
            return new UnmodifiableStatisticException(MessageFormat.format("statistic of name: {0} is unmodifiable", name));
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
