package pl.app.tribe.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.tribe.application.domain.Diplomacy;
import pl.app.tribe.application.domain.Tribe;

import java.io.Serializable;

public interface TribeCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateTribeCommand implements Serializable {
        private ObjectId founderId;
        private String name;
        private String abbreviation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddMemberCommand implements Serializable {
        private ObjectId tribeId;
        private ObjectId memberId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveMemberCommand implements Serializable {
        private ObjectId tribeId;
        private ObjectId memberId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ChangeMemberTypeCommand implements Serializable {
        private ObjectId tribeId;
        private ObjectId memberId;
        private Tribe.MemberType type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddDiplomacyCommand implements Serializable {
        private ObjectId tribeId;
        private ObjectId tribe2Id;
        private Diplomacy.DiplomacyType type;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveDiplomacyCommand implements Serializable {
        private ObjectId tribeId;
        private ObjectId tribe2Id;
        private Diplomacy.DiplomacyType type;
    }
}
