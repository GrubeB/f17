package pl.app.tower_attack.application.domain;

import pl.app.common.shared.exception.InvalidStateException;
import pl.app.common.shared.exception.NotFoundException;

public interface TowerAttackException {
    class NotFoundTowerAttackResultException extends NotFoundException {
        public NotFoundTowerAttackResultException() {
            super("not found tower attack result");
        }

        public NotFoundTowerAttackResultException(String message) {
            super(message);
        }

        public static NotFoundTowerAttackResultException fromId(String id) {
            return new NotFoundTowerAttackResultException("not found tower attack result with id: " + id);
        }
    }

    class NotEnoughEnergyException extends InvalidStateException {
        public NotEnoughEnergyException() {
            super("not enough energy to start attack");
        }

        public NotEnoughEnergyException(String message) {
            super(message);
        }
    }
}
