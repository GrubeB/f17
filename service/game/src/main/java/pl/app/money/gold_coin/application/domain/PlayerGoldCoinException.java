package pl.app.money.gold_coin.application.domain;

import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface PlayerGoldCoinException {
    class NotFoundPlayerGoldCoinException extends NotFoundException {
        public NotFoundPlayerGoldCoinException() {
            super("not found player money");
        }

        public NotFoundPlayerGoldCoinException(String message) {
            super(message);
        }

        public static NotFoundPlayerGoldCoinException fromId(String id) {
            return new NotFoundPlayerGoldCoinException("not found player money: " + id);
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

    class VillageDoseNotMeetRequirementsException extends InvalidStateException {
        public VillageDoseNotMeetRequirementsException() {
            super("village does not meet requirements");
        }

        public VillageDoseNotMeetRequirementsException(String message) {
            super(message);
        }
    }
}
