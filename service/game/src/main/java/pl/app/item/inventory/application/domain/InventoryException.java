package pl.app.item.inventory.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface InventoryException {
    class NotFoundInventoryException extends NotFoundException {
        public NotFoundInventoryException() {
            super("not found inventory");
        }

        public NotFoundInventoryException(String message) {
            super(message);
        }

        public static NotFoundInventoryException fromId(String id) {
            return new NotFoundInventoryException("not found inventory: " + id);
        }
    }

    class DuplicatedPlayerIdException extends ValidationException {
        public DuplicatedPlayerIdException() {
            super("there is object for given player");
        }

        public DuplicatedPlayerIdException(String message) {
            super(message);
        }

        public static DuplicatedPlayerIdException fromId(String id) {
            return new DuplicatedPlayerIdException(MessageFormat.format("there is object for given player: {0}", id));
        }
    }

    class NotFoundItemException extends NotFoundException {
        public NotFoundItemException() {
            super("not found item in inventory");
        }

        public NotFoundItemException(String message) {
            super(message);
        }
    }

    class CanNotUseItemException extends IllegalArgumentException {
        public CanNotUseItemException() {
            super("can not use item in inventory");
        }

        public CanNotUseItemException(String message) {
            super(message);
        }
    }
}
