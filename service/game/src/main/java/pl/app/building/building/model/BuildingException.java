package pl.app.building.building.model;

import pl.app.common.shared.exception.IllegalArgumentException;

public interface BuildingException {
    class InvalidBuildingLevelException extends IllegalArgumentException {
        public InvalidBuildingLevelException() {
            super("invalid building level");
        }

        public InvalidBuildingLevelException(String message) {
            super(message);
        }
    }
}
