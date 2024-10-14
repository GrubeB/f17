package pl.app.map.map.application.domain;

import pl.app.common.shared.exception.NotFoundException;

public interface MapException {
    class NotFoundMapException extends NotFoundException {
        public NotFoundMapException() {
            super("not found map");
        }

        public NotFoundMapException(String message) {
            super(message);
        }
    }

    class NotFoundPositionException extends NotFoundException {
        public NotFoundPositionException() {
            super("not found position on map");
        }

        public NotFoundPositionException(String message) {
            super(message);
        }
    }
}
