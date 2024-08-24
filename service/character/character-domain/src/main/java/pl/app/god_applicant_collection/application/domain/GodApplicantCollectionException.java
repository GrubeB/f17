package pl.app.god_applicant_collection.application.domain;

import pl.app.common.shared.exception.IllegalArgumentException;
import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface GodApplicantCollectionException {
    class NotFoundGodApplicantCollectionException extends NotFoundException {
        public NotFoundGodApplicantCollectionException() {
            super("not found god applicant collection");
        }

        public NotFoundGodApplicantCollectionException(String message) {
            super(message);
        }

        public static NotFoundGodApplicantCollectionException fromGodId(String id) {
            return new NotFoundGodApplicantCollectionException(MessageFormat.format("not found god applicant collection for god :{0}", id));
        }
    }

    class DuplicatedGodsException extends ValidationException {
        public DuplicatedGodsException() {
            super("god applicant collection exists for a god");
        }

        public DuplicatedGodsException(String message) {
            super(message);
        }

        public static DuplicatedGodsException fromGodId(String id) {
            return new DuplicatedGodsException(MessageFormat.format("god applicant collection exists for a god: {0}", id));
        }
    }
    class DuplicatedCharacterException extends ValidationException {
        public DuplicatedCharacterException() {
            super("character is already applicant");
        }

        public DuplicatedCharacterException(String message) {
            super(message);
        }

        public static DuplicatedCharacterException fromCharacterId(String id) {
            return new DuplicatedCharacterException(MessageFormat.format("character {0} is already applicant", id));
        }
    }
    class NotFoundGodApplicantException extends NotFoundException {
        public NotFoundGodApplicantException() {
            super("not found god applicant");
        }

        public NotFoundGodApplicantException(String message) {
            super(message);
        }

        public static NotFoundGodApplicantException fromCharacterId(String characterId) {
            return new NotFoundGodApplicantException(MessageFormat.format("not found god applicant for character :{0}", characterId));
        }
    }


}
