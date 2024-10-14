package pl.app.resource.village_resource.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface VillageResourceException {
    class NotFoundVillageResourceException extends NotFoundException {
        public NotFoundVillageResourceException() {
            super("not found village resource");
        }

        public NotFoundVillageResourceException(String message) {
            super(message);
        }

        public static NotFoundVillageResourceException fromId(String id) {
            return new NotFoundVillageResourceException("not found village resource: " + id);
        }
    }

    class DuplicatedVillageResourceException extends ValidationException {
        public DuplicatedVillageResourceException() {
            super("there are resources for given village");
        }

        public DuplicatedVillageResourceException(String message) {
            super(message);
        }

        public static DuplicatedVillageResourceException fromId(String id) {
            return new DuplicatedVillageResourceException(MessageFormat.format("there are resources for given village: {0}", id));
        }
    }

    class InsufficientResourceException extends InvalidStateException {
        public InsufficientResourceException() {
            super("insufficient resources");
        }

        public InsufficientResourceException(String message) {
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
