package pl.app.account_equipment.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface AccountEquipmentCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateAccountEquipmentCommand implements Serializable {
        private ObjectId accountId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddItemToAccountEquipmentCommand implements Serializable {
        private ObjectId accountId;
        private ObjectId itemId;
        private String itemType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveItemToAccountEquipmentCommand implements Serializable {
        private ObjectId accountId;
        private ObjectId itemId;
        private String itemType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SetCharacterItemCommand implements Serializable {
        private ObjectId accountId;
        private ObjectId characterId;
        private String slot;
        private ObjectId itemId;
        private String itemType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveCharacterItemCommand implements Serializable {
        private ObjectId accountId;
        private ObjectId characterId;
        private String slot;
    }
}
