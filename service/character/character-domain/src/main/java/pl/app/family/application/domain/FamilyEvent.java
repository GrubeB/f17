package pl.app.family.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface FamilyEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class FamilyCreatedEvent implements Serializable {
        private ObjectId familyId;
        private ObjectId godId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterAddedToFamilyEvent implements Serializable {
        private ObjectId familyId;
        private ObjectId godId;
        private ObjectId characterId;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterRemovedFromFamilyEvent implements Serializable {
        private ObjectId familyId;
        private ObjectId godId;
        private ObjectId characterId;
    }
}
