package god_family.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GodFamilyException {
    class NotFoundGodFamilyException extends NotFoundException {
        public NotFoundGodFamilyException() {
            super("not found family");
        }

        public NotFoundGodFamilyException(String message) {
            super(message);
        }

        public static NotFoundGodFamilyException fromGodId(String id) {
            return new NotFoundGodFamilyException(MessageFormat.format("not found family for god :{0}", id));
        }
    }

    class DuplicatedGodsException extends ValidationException {
        public DuplicatedGodsException() {
            super("family exists for a god");
        }

        public DuplicatedGodsException(String message) {
            super(message);
        }

        public static DuplicatedGodsException fromGodId(String id) {
            return new DuplicatedGodsException(MessageFormat.format("family exists for a god: {0}", id));
        }
    }

    class NoSuchStatisticException extends IllegalArgumentException {
        public NoSuchStatisticException() {
            super("there is not statistic");
        }

        public NoSuchStatisticException(String message) {
            super(message);
        }

        public static NoSuchStatisticException fromName(String name) {
            return new NoSuchStatisticException(MessageFormat.format("there is not statistic of name: {0}", name));
        }
    }

    class UnmodifiableStatisticException extends IllegalArgumentException {
        public UnmodifiableStatisticException() {
            super("statistic is unmodifiable");
        }

        public UnmodifiableStatisticException(String message) {
            super(message);
        }

        public static UnmodifiableStatisticException fromName(String name) {
            return new UnmodifiableStatisticException(MessageFormat.format("statistic of name: {0} is unmodifiable", name));
        }
    }


}
