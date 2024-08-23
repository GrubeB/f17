package pl.app.item_template.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface ItemTemplateException {
    class NotFoundItemTemplateException extends NotFoundException {
        public NotFoundItemTemplateException() {
            super("not found item template");
        }

        public NotFoundItemTemplateException(String message) {
            super(message);
        }

        public static NotFoundItemTemplateException fromId(String id) {
            return new NotFoundItemTemplateException("not found item template with id: " + id);
        }
    }

    class DuplicatedNameException extends ValidationException {
        public DuplicatedNameException() {
            super("there is a item template for given name");
        }

        public DuplicatedNameException(String message) {
            super(message);
        }

        public static DuplicatedNameException fromName(String name) {
            return new DuplicatedNameException(MessageFormat.format("there is a item template for given name: {0}", name));
        }
    }
}
