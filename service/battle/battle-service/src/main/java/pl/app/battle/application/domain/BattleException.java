package pl.app.battle.application.domain;

import pl.app.common.shared.exception.NotFoundException;

public interface BattleException {
    class NotFoundCharacterException extends NotFoundException {
        public NotFoundCharacterException() {
            super("not found character");
        }

        public NotFoundCharacterException(String message) {
            super(message);
        }

        public static NotFoundCharacterException fromId(String id) {
            return new NotFoundCharacterException("not found character with id: " + id);
        }
    }
}
