package pl.app.god_equipment.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface CharacterGearCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateCharacterGearCommand implements Serializable {
        private ObjectId characterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterGearCommand implements Serializable {
        private ObjectId characterId;
    }
}
