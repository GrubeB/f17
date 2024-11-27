package pl.app.village.loyalty.application.domain;

import pl.app.common.shared.exception.NotFoundException;

public interface VillageLoyaltyException {
    class NotFoundVillageLoyaltyException extends NotFoundException {
        public NotFoundVillageLoyaltyException() {
            super("not found village loyalty");
        }

        public NotFoundVillageLoyaltyException(String message) {
            super(message);
        }

        public static NotFoundVillageLoyaltyException fromId(String id) {
            return new NotFoundVillageLoyaltyException("not found village loyalty: " + id);
        }
    }
}
