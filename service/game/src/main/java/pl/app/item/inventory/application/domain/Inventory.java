package pl.app.item.inventory.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.item.item.application.domain.Item;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@Document(collection = "inventory")
@NoArgsConstructor
public class Inventory {
    @Id
    private ObjectId playerId;
    private List<InventoryItem> inventoryItems;

    public Inventory(ObjectId playerId) {
        this.playerId = playerId;
        this.inventoryItems = new LinkedList<>();
    }

    public void addItem(Item item, Integer amount) {
        Optional<InventoryItem> inventoryItem = getItem(item);
        if (inventoryItem.isPresent()) {
            inventoryItem.get().increment(amount);
            return;
        }
        inventoryItems.add(new InventoryItem(item, amount));
    }

    public void removeItem(Item item, Integer amount) {
        Optional<InventoryItem> inventoryItemOpt = getItem(item);
        if (inventoryItemOpt.isEmpty() || inventoryItemOpt.get().getAmount() - amount < 0) {
            throw new InventoryException.NotFoundItemException();
        }

        InventoryItem inventoryItem = inventoryItemOpt.get();
        inventoryItem.decrement(amount);
        if (inventoryItem.getAmount() == 0) {
            inventoryItems.remove(inventoryItem);
        }
    }

    public Optional<InventoryItem> getItem(Item item) {
        return inventoryItems.stream()
                .filter(e -> e.getItem().equals(item))
                .findAny();
    }

    @Getter
    @NoArgsConstructor
    public static class InventoryItem {
        private Item item;
        private Integer amount;

        public InventoryItem(Item item, Integer amount) {
            this.item = item;
            this.amount = amount;
        }

        public void increment(Integer amount) {
            this.amount += amount;
        }

        public void decrement(Integer amount) {
            this.amount -= amount;
        }
    }
}
