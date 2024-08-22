package pl.app.god_equipment.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface GodEquipmentEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodEquipmentCreatedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
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
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterItemSetEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
        private ObjectId characterId;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterItemRemovedEvent implements Serializable {
        private ObjectId godEquipmentId;
        private ObjectId godId;
        private ObjectId itemId;
        private String itemType;
        private ObjectId characterId;
    }
}
