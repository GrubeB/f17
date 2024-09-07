package pl.app.tower.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;
import pl.app.monster_template.application.domain.MonsterTemplateException;

import java.text.MessageFormat;

public interface TowerException {
    class NotFoundTowerLevelException extends NotFoundException {
        public NotFoundTowerLevelException() {
            super("not found tower level");
        }

        public NotFoundTowerLevelException(String message) {
            super(message);
        }

        public static NotFoundTowerLevelException fromLevel(Integer level) {
            return new NotFoundTowerLevelException("not found tower level with level: " + level);
        }
    }
    class DuplicatedLevelException extends ValidationException {
        public DuplicatedLevelException() {
            super("there is a tower level for given level");
        }

        public DuplicatedLevelException(String message) {
            super(message);
        }

        public static DuplicatedLevelException fromLevel(Integer level) {
            return new DuplicatedLevelException(MessageFormat.format("there is a tower level for given level: {0}", level));
        }
    }
}
