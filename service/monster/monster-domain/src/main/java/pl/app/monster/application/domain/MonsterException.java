package pl.app.monster.application.domain;

import pl.app.common.shared.exception.NotFoundException;

public interface MonsterException {
    class NotFoundMonsterException extends NotFoundException {
        public NotFoundMonsterException() {
            super("not found monster");
        }

        public NotFoundMonsterException(String message) {
            super(message);
        }

        public static NotFoundMonsterException fromId(String id) {
            return new NotFoundMonsterException("not found monster with id: " + id);
        }
    }

}
