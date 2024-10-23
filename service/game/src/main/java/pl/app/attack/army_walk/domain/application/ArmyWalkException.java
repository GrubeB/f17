package pl.app.attack.army_walk.domain.application;

import pl.app.common.shared.exception.NotFoundException;

public interface ArmyWalkException {
    class NotFoundArmyWalkException extends NotFoundException {
        public NotFoundArmyWalkException() {
            super("not found army walk");
        }

        public NotFoundArmyWalkException(String message) {
            super(message);
        }

        public static NotFoundArmyWalkException fromId(String id) {
            return new NotFoundArmyWalkException("not found army walk: " + id);
        }
    }
}
