package pl.app.unit.village_army.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface VillageArmyException {
    class NotFoundVillageArmyException extends NotFoundException {
        public NotFoundVillageArmyException() {
            super("not found village army");
        }

        public NotFoundVillageArmyException(String message) {
            super(message);
        }

        public static NotFoundVillageArmyException fromId(String id) {
            return new NotFoundVillageArmyException("not found village army: " + id);
        }
    }

    class DuplicatedVillageArmyException extends ValidationException {
        public DuplicatedVillageArmyException() {
            super("there are army for given village");
        }

        public DuplicatedVillageArmyException(String message) {
            super(message);
        }

        public static DuplicatedVillageArmyException fromId(String id) {
            return new DuplicatedVillageArmyException(MessageFormat.format("there are army for given village: {0}", id));
        }
    }
}
