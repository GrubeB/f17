package god_family.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface GodFamilyEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodFamilyCreatedEvent implements Serializable {
        private ObjectId godFamilyId;
        private ObjectId godId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterAddedToFamilyEvent implements Serializable {
        private ObjectId godFamilyId;
        private ObjectId godId;
        private ObjectId characterId;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterRemovedFromFamilyEvent implements Serializable {
        private ObjectId godFamilyId;
        private ObjectId godId;
        private ObjectId characterId;
    }
}
