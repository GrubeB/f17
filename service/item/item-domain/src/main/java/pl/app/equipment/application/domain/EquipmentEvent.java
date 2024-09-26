package pl.app.equipment.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface EquipmentEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class EquipmentCreatedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterGearAddedToEquipmentEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId characterGearId;
        private ObjectId characterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterGearRemovedFromEquipmentEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId characterGearId;
        private ObjectId characterId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class EquipmentItemAddedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class EquipmentItemRemovedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
    }
}
