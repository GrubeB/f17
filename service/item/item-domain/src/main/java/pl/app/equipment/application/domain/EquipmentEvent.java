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
    class GodEquipmentCreatedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterGearCreatedEvent implements Serializable {
        private ObjectId characterGearId;
        private ObjectId characterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterGearRemovedEvent implements Serializable {
        private ObjectId characterGearId;
        private ObjectId characterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterGearAddedToGodEquipmentEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId characterGearId;
        private ObjectId characterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterGearRemovedFromGodEquipmentEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId characterGearId;
        private ObjectId characterId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodEquipmentItemAddedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodEquipmentItemRemovedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
    }
}
