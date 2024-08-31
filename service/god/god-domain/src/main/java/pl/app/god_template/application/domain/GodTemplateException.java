package pl.app.god_template.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GodTemplateException {
    class NotFoundGodTemplateException extends NotFoundException {
        public NotFoundGodTemplateException() {
            super("not found god template");
        }

        public NotFoundGodTemplateException(String message) {
            super(message);
        }

        public static NotFoundGodTemplateException fromId(String id) {
            return new NotFoundGodTemplateException("not found god template with id: " + id);
        }
    }
    class DuplicatedNameException extends ValidationException {
        public DuplicatedNameException() {
            super("there is a template for given name");
        }

        public DuplicatedNameException(String message) {
            super(message);
        }

        public static DuplicatedNameException fromName(String name) {
            return new DuplicatedNameException(MessageFormat.format("there is a template for given name: {0}", name));
        }
    }
}

