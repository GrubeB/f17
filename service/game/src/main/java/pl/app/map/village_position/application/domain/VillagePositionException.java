package pl.app.map.village_position.application.domain;

import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface VillagePositionException {
    class NoMoreVillagePositionRemainedException extends InvalidStateException {
        public NoMoreVillagePositionRemainedException() {
            super("no more village positions remained to create new village");
        }

        public NoMoreVillagePositionRemainedException(String message) {
            super(message);
        }
    }

    class DuplicatedVillagePositionException extends ValidationException {
        public DuplicatedVillagePositionException() {
            super("there is a position for given village");
        }

        public DuplicatedVillagePositionException(String message) {
            super(message);
        }

        public static DuplicatedVillagePositionException fromId(String id) {
            return new DuplicatedVillagePositionException(MessageFormat.format("there is a position for given village: {0}", id));
        }
    }
}
