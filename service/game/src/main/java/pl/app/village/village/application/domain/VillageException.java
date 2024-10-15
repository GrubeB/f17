package pl.app.village.village.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface VillageException {
    class NotFoundVillageException extends NotFoundException {
        public NotFoundVillageException() {
            super("not found village");
        }

        public NotFoundVillageException(String message) {
            super(message);
        }

        public static NotFoundVillageException fromId(String id) {
            return new NotFoundVillageException("not found village: " + id);
        }
    }
}
