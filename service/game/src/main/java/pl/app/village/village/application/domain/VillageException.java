package pl.app.village.village.application.domain;

import pl.app.common.shared.exception.NotFoundException;

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
