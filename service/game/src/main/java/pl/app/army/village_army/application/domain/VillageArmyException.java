package pl.app.army.village_army.application.domain;

import pl.app.common.shared.exception.InvalidStateException;
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

    class VillageDoseNotSupportException extends InvalidStateException {
        public VillageDoseNotSupportException() {
            super("this village does not support given village");
        }

        public VillageDoseNotSupportException(String message) {
            super(message);
        }

    }
}
