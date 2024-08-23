package pl.app.god_equipment.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GodEquipmentException {
    class NotFoundGodEquipmentException extends NotFoundException {
        public NotFoundGodEquipmentException() {
            super("not found equipment");
        }

        public NotFoundGodEquipmentException(String message) {
            super(message);
        }

        public static NotFoundGodEquipmentException fromId(String id) {
            return new NotFoundGodEquipmentException("not found equipment with id: " + id);
        }

        public static NotFoundGodEquipmentException fromGodId(String id) {
            return new NotFoundGodEquipmentException("not found equipment for god: " + id);
        }
    }

    class DuplicatedAccountException extends ValidationException {
        public DuplicatedAccountException() {
            super("there is a equipment for given account");
        }

        public DuplicatedAccountException(String message) {
            super(message);
        }

        public static DuplicatedAccountException fromId(String id) {
            return new DuplicatedAccountException(MessageFormat.format("there is a equipment for given account: {0}", id));
        }
    }
}
