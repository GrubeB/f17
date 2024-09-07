package pl.app.monster_gear.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface MonsterGearException {
    class NotFoundMonsterGearException extends NotFoundException {
        public NotFoundMonsterGearException() {
            super("not found monster gear");
        }

        public NotFoundMonsterGearException(String message) {
            super(message);
        }

        public static NotFoundMonsterGearException fromId(String id) {
            return new NotFoundMonsterGearException("not found monster gear with id: " + id);
        }
    }
    class DuplicatedMonsterGearException extends ValidationException {
        public DuplicatedMonsterGearException() {
            super("there is a gear for given monster");
        }

        public DuplicatedMonsterGearException(String message) {
            super(message);
        }

        public static DuplicatedMonsterGearException fromId(String id) {
            return new DuplicatedMonsterGearException(MessageFormat.format("there is a gear for given monster: {0}", id));
        }
    }
    class WrongSlotException extends IllegalArgumentException {
        public WrongSlotException() {
            super("item cannot be placed here");
        }

        public WrongSlotException(String message) {
            super(message);
        }
    }
}
