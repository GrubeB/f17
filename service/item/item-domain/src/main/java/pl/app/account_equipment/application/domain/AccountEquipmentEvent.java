package pl.app.account_equipment.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface AccountEquipmentEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class AccountEquipmentCreatedEvent implements Serializable {
        private ObjectId accountEquipmentId;
        private ObjectId accountId;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class AccountEquipmentItemAddedEvent implements Serializable {
        private ObjectId accountEquipmentId;
        private ObjectId accountId;
        private ObjectId itemId;
        private String itemType;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class AccountEquipmentItemRemovedEvent implements Serializable {
        private ObjectId accountEquipmentId;
        private ObjectId accountId;
        private ObjectId itemId;
        private String itemType;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterItemSetEvent implements Serializable {
        private ObjectId accountEquipmentId;
        private ObjectId accountId;
        private ObjectId itemId;
        private String itemType;
        private ObjectId characterId;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterItemRemovedEvent implements Serializable {
        private ObjectId accountEquipmentId;
        private ObjectId accountId;
        private ObjectId itemId;
        private String itemType;
        private ObjectId characterId;
    }
}
