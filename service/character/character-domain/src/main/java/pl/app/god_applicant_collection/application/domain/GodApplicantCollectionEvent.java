package pl.app.god_applicant_collection.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface GodApplicantCollectionEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodApplicantCollectionCreatedEvent implements Serializable {
        private ObjectId godApplicationCollectionId;
        private ObjectId godId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodApplicantCreatedEvent implements Serializable {
        private ObjectId godApplicationCollectionId;
        private ObjectId godId;
        private ObjectId applicationId;
        private ObjectId characterId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodApplicantRemovedEvent implements Serializable {
        private ObjectId godApplicationCollectionId;
        private ObjectId godId;
        private ObjectId applicationId;
        private ObjectId characterId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodApplicantAcceptedEvent implements Serializable {
        private ObjectId godApplicationCollectionId;
        private ObjectId godId;
        private ObjectId applicationId;
        private ObjectId characterId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodApplicantRejectedEvent implements Serializable {
        private ObjectId godApplicationCollectionId;
        private ObjectId godId;
        private ObjectId applicationId;
        private ObjectId characterId;
    }
}
