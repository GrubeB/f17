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
    class NotFoundCharacterGearException extends NotFoundException {
        public NotFoundCharacterGearException() {
            super("not found character gear");
        }

        public NotFoundCharacterGearException(String message) {
            super(message);
        }

        public static NotFoundCharacterGearException fromCharacterId(String characterId) {
            return new NotFoundCharacterGearException("not found character gear for character: " + characterId);
        }
    }
    class DuplicatedGodEquipmentException extends ValidationException {
        public DuplicatedGodEquipmentException() {
            super("there is a equipment for given god");
        }

        public DuplicatedGodEquipmentException(String message) {
            super(message);
        }

        public static DuplicatedGodEquipmentException fromId(String id) {
            return new DuplicatedGodEquipmentException(MessageFormat.format("there is a equipment for given god: {0}", id));
        }
    }
    class DuplicatedCharacterGearException extends ValidationException {
        public DuplicatedCharacterGearException() {
            super("there is a gear for given character");
        }

        public DuplicatedCharacterGearException(String message) {
            super(message);
        }

        public static DuplicatedCharacterGearException fromId(String id) {
            return new DuplicatedCharacterGearException(MessageFormat.format("there is a gear for given character: {0}", id));
        }
    }
}
