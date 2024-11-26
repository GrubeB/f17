package pl.app.inventory.inventory.application.domain;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.inventory.shared.OfficerItem;

import static pl.app.inventory.shared.OfficerType.GRANDMASTER;

class InventoryTest {

    @Test
    void addItem_shouldAddItems() {
        Inventory inventory = new Inventory(ObjectId.get());
        OfficerItem officerItem = new OfficerItem(GRANDMASTER);
        inventory.addItem(officerItem, 10);
        Assertions.assertThat(inventory.getItem(new OfficerItem(GRANDMASTER)).isPresent()).isTrue();
        Assertions.assertThat(inventory.getItem(new OfficerItem(GRANDMASTER)).get().getAmount()).isEqualTo(10);
    }

    @Test
    void removeItem() {
        Inventory inventory = new Inventory(ObjectId.get());
        inventory.addItem(new OfficerItem(GRANDMASTER), 10);
        inventory.removeItem(new OfficerItem(GRANDMASTER), 9);
        Assertions.assertThat(inventory.getItem(new OfficerItem(GRANDMASTER)).get().getAmount()).isEqualTo(1);
    }
}