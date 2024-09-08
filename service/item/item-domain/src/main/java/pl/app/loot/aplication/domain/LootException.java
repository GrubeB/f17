package pl.app.loot.aplication.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface LootException {
    class NotFoundLootException extends NotFoundException {
        public NotFoundLootException() {
            super("not found loot");
        }

        public NotFoundLootException(String message) {
            super(message);
        }

        public static NotFoundLootException fromId(String id) {
            return new NotFoundLootException("not found loot with domain object id: " + id);
        }
    }

    class DuplicatedLootException extends ValidationException {
        public DuplicatedLootException() {
            super("there is a loot for given domain object");
        }

        public DuplicatedLootException(String message) {
            super(message);
        }

        public static DuplicatedLootException fromId(String id) {
            return new DuplicatedLootException(MessageFormat.format("there is a loot for given domain object: {0}", id));
        }
    }
}
