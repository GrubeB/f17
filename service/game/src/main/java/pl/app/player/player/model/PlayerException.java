package pl.app.player.player.model;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface PlayerException {
    class NotFoundPlayerException extends NotFoundException {
        public NotFoundPlayerException() {
            super("not found player");
        }

        public NotFoundPlayerException(String message) {
            super(message);
        }

        public static NotFoundPlayerException fromId(String id) {
            return new NotFoundPlayerException("not found player: " + id);
        }
    }

    class DuplicatedPlayerAccountIdException extends ValidationException {
        public DuplicatedPlayerAccountIdException() {
            super("there is a player with given name");
        }

        public DuplicatedPlayerAccountIdException(String message) {
            super(message);
        }

        public static DuplicatedPlayerAccountIdException fromAccountId(String name) {
            return new DuplicatedPlayerAccountIdException(MessageFormat.format("there is a player with given: {0}", name));
        }
    }
}
