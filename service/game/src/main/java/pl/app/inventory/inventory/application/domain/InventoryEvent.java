package pl.app.inventory.inventory.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.inventory.shared.Item;

import java.io.Serializable;

public interface InventoryEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class PlayerInventoryCreatedEvent implements Serializable {
        private ObjectId playerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ItemAddedEvent implements Serializable {
        private ObjectId playerId;
        private Item item;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ItemRemovedEvent implements Serializable {
        private ObjectId playerId;
        private Item item;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ItemUsedEvent implements Serializable {
        private ObjectId playerId;
        private Item item;
        private Integer amount;
        private ObjectId domainObjectId; // depends on context, villageId or playerId
    }
}
