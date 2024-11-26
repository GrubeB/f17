package pl.app.inventory.shared;


import lombok.Getter;

import java.util.Objects;

@Getter
public class Item {
    private ItemType itemType;

    public Item() {
    }

    public Item(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return itemType == item.itemType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(itemType);
    }
}
