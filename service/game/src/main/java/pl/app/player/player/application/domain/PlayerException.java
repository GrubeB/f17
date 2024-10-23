package pl.app.player.player.application.domain;

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

    class DuplicatedPlayerNameException extends ValidationException {
        public DuplicatedPlayerNameException() {
            super("there is a player with given name");
        }

        public DuplicatedPlayerNameException(String message) {
            super(message);
        }

        public static DuplicatedPlayerNameException fromName(String name) {
            return new DuplicatedPlayerNameException(MessageFormat.format("there is a player with given: {0}", name));
        }
    }
}
