package pl.app.inventory.inventory.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.inventory.shared.Item;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto implements Serializable {
    private ObjectId playerId;
    private List<InventoryItemDto> inventoryItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InventoryItemDto implements Serializable {
        private Item item;
        private Integer amount;
    }
}