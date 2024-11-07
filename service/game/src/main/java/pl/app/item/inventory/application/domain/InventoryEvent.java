package pl.app.item.inventory.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface InventoryEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class PlayerInventoryCreatedEvent implements Serializable {
        private ObjectId playerId;
    }
}
