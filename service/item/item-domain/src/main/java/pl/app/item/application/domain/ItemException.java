package pl.app.item.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface ItemException {
    class NotFoundItemException extends NotFoundException {
        public NotFoundItemException() {
            super("not found item");
        }

        public NotFoundItemException(String message) {
            super(message);
        }

        public static NotFoundItemException fromId(String id) {
            return new NotFoundItemException("not found item with id: " + id);
        }
    }
}
