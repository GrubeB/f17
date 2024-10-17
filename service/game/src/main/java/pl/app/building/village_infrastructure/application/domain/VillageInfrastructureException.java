package pl.app.building.village_infrastructure.application.domain;

import pl.app.common.shared.exception.NotFoundException;

public interface VillageInfrastructureException {
    class NotFoundVillageInfrastructureException extends NotFoundException {
        public NotFoundVillageInfrastructureException() {
            super("not found village infrastructure");
        }

        public NotFoundVillageInfrastructureException(String message) {
            super(message);
        }

        public static NotFoundVillageInfrastructureException fromId(String id) {
            return new NotFoundVillageInfrastructureException("not found village infrastructure: " + id);
        }
    }
}
