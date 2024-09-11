package pl.app.family.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface FamilyCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGodFamilyCommand implements Serializable {
        private ObjectId godId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddCharacterToGodFamilyCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterFromGodFamilyCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }
}
