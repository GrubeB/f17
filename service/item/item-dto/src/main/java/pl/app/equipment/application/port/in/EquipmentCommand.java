package pl.app.equipment.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface EquipmentCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateEquipmentCommand implements Serializable {
        private ObjectId godId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddCharacterGearToEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterGearFromEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddItemToEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveItemFromEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
    }
}
