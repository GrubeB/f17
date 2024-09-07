package pl.app.monster_template.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface MonsterTemplateException {
    class NotFoundMonsterTemplateException extends NotFoundException {
        public NotFoundMonsterTemplateException() {
            super("not found monster template");
        }

        public NotFoundMonsterTemplateException(String message) {
            super(message);
        }

        public static NotFoundMonsterTemplateException fromId(String id) {
            return new NotFoundMonsterTemplateException("not found monster template with id: " + id);
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

