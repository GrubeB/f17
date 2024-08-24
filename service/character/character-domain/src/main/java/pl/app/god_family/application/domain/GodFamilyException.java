package pl.app.god_family.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GodFamilyException {
    class NotFoundGodFamilyException extends NotFoundException {
        public NotFoundGodFamilyException() {
            super("not found family");
        }

        public NotFoundGodFamilyException(String message) {
            super(message);
        }

        public static NotFoundGodFamilyException fromGodId(String id) {
            return new NotFoundGodFamilyException(MessageFormat.format("not found family for god :{0}", id));
        }
    }

    class DuplicatedGodsException extends ValidationException {
        public DuplicatedGodsException() {
            super("family exists for a god");
        }

        public DuplicatedGodsException(String message) {
            super(message);
        }

        public static DuplicatedGodsException fromGodId(String id) {
            return new DuplicatedGodsException(MessageFormat.format("family exists for a god: {0}", id));
        }
    }

    class CharacterBelongsToFamilyException extends IllegalArgumentException {
        public CharacterBelongsToFamilyException() {
            super("character already belongs to the family");
        }

        public CharacterBelongsToFamilyException(String message) {
            super(message);
        }

        public static CharacterBelongsToFamilyException fromId(String characterId) {
            return new CharacterBelongsToFamilyException(MessageFormat.format("character {0} already belongs to the family\"", characterId));
        }
    }

}
