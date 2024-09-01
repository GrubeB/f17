package pl.app.character_template.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface CharacterTemplateException {
    class NotFoundCharacterTemplateException extends NotFoundException {
        public NotFoundCharacterTemplateException() {
            super("not found character template");
        }

        public NotFoundCharacterTemplateException(String message) {
            super(message);
        }

        public static NotFoundCharacterTemplateException fromId(String id) {
            return new NotFoundCharacterTemplateException("not found character template with id: " + id);
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

