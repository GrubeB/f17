package pl.app.gear.aplication.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GearException {
    class NotFoundGearException extends NotFoundException {
        public NotFoundGearException() {
            super("not found gear");
        }

        public NotFoundGearException(String message) {
            super(message);
        }

        public static NotFoundGearException fromId(String id) {
            return new NotFoundGearException("not found gear with id: " + id);
        }
    }
    class DuplicatedGearException extends ValidationException {
        public DuplicatedGearException() {
            super("there is a gear for given");
        }

        public DuplicatedGearException(String message) {
            super(message);
        }

        public static DuplicatedGearException fromId(String id) {
            return new DuplicatedGearException(MessageFormat.format("there is a gear for given: {0}", id));
        }
    }
    class WrongSlotException extends IllegalArgumentException {
        public WrongSlotException() {
            super("item cannot be placed here");
        }

        public WrongSlotException(String message) {
            super(message);
        }
    }
}
