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
    class CreateGodEquipmentCommand implements Serializable {
        private ObjectId godId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddCharacterGearToGodEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterGearFromGodEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddItemToGodEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveItemFromGodEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
    }
}
