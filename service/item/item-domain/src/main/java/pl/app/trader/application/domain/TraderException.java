package pl.app.trader.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface TraderException {
    class NotFoundTraderForGodException extends NotFoundException {
        public NotFoundTraderForGodException() {
            super("not found trader");
        }

        public NotFoundTraderForGodException(String message) {
            super(message);
        }

        public static NotFoundTraderForGodException fromGodId(String id) {
            return new NotFoundTraderForGodException("not found trader for god: " + id);
        }
    }
    class NotFoundItemException extends NotFoundException {
        public NotFoundItemException() {
            super("not found item in store");
        }

        public NotFoundItemException(String message) {
            super(message);
        }

        public static NotFoundItemException fromItemId(String itemId) {
            return new NotFoundItemException("not found item in store: " + itemId);
        }
    }
    class DuplicatedGodException extends ValidationException {
        public DuplicatedGodException() {
            super("there is a trader for given god");
        }

        public DuplicatedGodException(String message) {
            super(message);
        }

        public static DuplicatedGodException fromId(String id) {
            return new DuplicatedGodException(MessageFormat.format("there is a trader for given god: {0}", id));
        }
    }
}
