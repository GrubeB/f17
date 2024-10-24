package pl.app.money.player_money.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface PlayerMoneyException {
    class NotFoundPlayerMoneyException extends NotFoundException {
        public NotFoundPlayerMoneyException() {
            super("not found player money");
        }

        public NotFoundPlayerMoneyException(String message) {
            super(message);
        }

        public static NotFoundPlayerMoneyException fromId(String id) {
            return new NotFoundPlayerMoneyException("not found player money: " + id);
        }
    }

    class DuplicatedPlayerIdException extends ValidationException {
        public DuplicatedPlayerIdException() {
            super("there is object for given player");
        }

        public DuplicatedPlayerIdException(String message) {
            super(message);
        }

        public static DuplicatedPlayerIdException fromId(String id) {
            return new DuplicatedPlayerIdException(MessageFormat.format("there is object for given player: {0}", id));
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
