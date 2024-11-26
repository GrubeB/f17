package pl.app.inventory.inventory.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.inventory.shared.Item;

import java.io.Serializable;

public interface InventoryCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateInventoryCommand implements Serializable {
        private ObjectId playerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddItemCommand implements Serializable {
        private ObjectId playerId;
        private Item item;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveItemCommand implements Serializable {
        private ObjectId playerId;
        private Item item;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UseItemCommand implements Serializable {
        private ObjectId playerId;
        private Item item;
        private Integer amount;
        private ObjectId domainObjectId; // depends on context, villageId or playerId
    }
}
