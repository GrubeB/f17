package pl.app.god_equipment.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface GodEquipmentCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGodEquipmentCommand implements Serializable {
        private ObjectId godId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddItemToGodEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveItemFromGodEquipmentCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SetCharacterItemCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
        private String slot;
        private ObjectId itemId;
        private String itemType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterItemCommand implements Serializable {
        private ObjectId godId;
        private ObjectId characterId;
        private String slot;
    }
}
