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
    class CreateFamilyCommand implements Serializable {
        private ObjectId godId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddCharacterToFamilyCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterFromFamilyCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }
}
