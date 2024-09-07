package pl.app.battle.application.domain;

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
}