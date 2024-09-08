package pl.app.loot.aplication.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import pl.app.common.shared.model.Money;
import pl.app.item_template.application.domain.ItemTemplate;

import java.util.Objects;
import java.util.Set;

@Getter
public class LootItem {
    @DBRef
    private ItemTemplate itemTemplate;
    private Integer chance; // 0 - 100_000
    private Integer amount;

    public LootItem(ItemTemplate itemTemplate, Integer chance, Integer amount) {
        this.itemTemplate = itemTemplate;
        this.chance = chance;
        this.amount = amount;
    }
    public void updateData(Integer chance, Integer amount){
        this.chance = chance;
        this.amount = amount;
    }
}
