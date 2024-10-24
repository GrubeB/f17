package pl.app.village.village_effect.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface VillageEffectException {
    class NotFoundVillageEffectException extends NotFoundException {
        public NotFoundVillageEffectException() {
            super("not found village effect");
        }

        public NotFoundVillageEffectException(String message) {
            super(message);
        }

        public static NotFoundVillageEffectException fromId(String id) {
            return new NotFoundVillageEffectException("not found village effect: " + id);
        }
    }

    class DuplicatedVillageIdException extends ValidationException {
        public DuplicatedVillageIdException() {
            super("there is object for given village");
        }

        public DuplicatedVillageIdException(String message) {
            super(message);
        }

        public static DuplicatedVillageIdException fromId(String id) {
            return new DuplicatedVillageIdException(MessageFormat.format("there is object for given village: {0}", id));
        }
    }

}
