package pl.app.energy.application.domain;

import org.bson.types.ObjectId;
import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;
import pl.app.god.application.domain.GodException;

import java.text.MessageFormat;

public interface EnergyException {
    class NotFoundEnergyException extends NotFoundException {
        public NotFoundEnergyException() {
            super("not found energy");
        }

        public NotFoundEnergyException(String message) {
            super(message);
        }

        public static NotFoundEnergyException fromGodId(String id) {
            return new NotFoundEnergyException("not found energy for god with id: " + id);
        }
    }
    class DuplicatedGodException extends ValidationException {
        public DuplicatedGodException() {
            super("there is a energy for given god");
        }

        public DuplicatedGodException(String message) {
            super(message);
        }

        public static DuplicatedGodException fromGodId(ObjectId godId) {
            return new DuplicatedGodException(MessageFormat.format("there is a energy for given god: {0}", godId.toHexString()));
        }
    }

    class InsufficientEnergyException extends InvalidStateException {
        public InsufficientEnergyException() {
            super("insufficient energy");
        }

        public InsufficientEnergyException(String message) {
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
