package pl.app.god_applicant_collection.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface GodApplicantCollectionCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGofApplicantCollectionCommand implements Serializable {
        private ObjectId godId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGodApplicantCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveGodApplicantCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AcceptGodApplicantCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RejectGodApplicantCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }
}
