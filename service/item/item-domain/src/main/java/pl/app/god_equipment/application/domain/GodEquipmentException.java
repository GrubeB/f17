package pl.app.god_equipment.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GodEquipmentException {
    class NotFoundItemException extends NotFoundException {
        public NotFoundItemException() {
            super("not found equipment");
        }

        public NotFoundItemException(String message) {
            super(message);
        }

        public static NotFoundItemException fromId(String id) {
            return new NotFoundItemException("not found equipment with id: " + id);
        }
        public static NotFoundItemException fromAccountId(String id) {
            return new NotFoundItemException("not found equipment for account: " + id);
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
