package pl.app.trader.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.item.application.domain.Item;
import pl.app.common.shared.model.ItemType;

import java.util.Set;

@Getter
@Document(collection = "traders")
public class Trader {
    @Id
    private ObjectId id;
    private ObjectId godId;

    @DBRef
    private Set<Item> items;

    @SuppressWarnings("unused")
    public Trader() {
    }

    public Trader(ObjectId godId, Set<Item> items) {
        this.id = ObjectId.get();
        this.godId = godId;
        this.items = items;
    }

    public void renewItems(Set<Item> items) {
        this.items = items;
    }

    public Item getItem( ObjectId itemId) {
        return this.items.stream().filter(i -> i.getId().equals(itemId) )
                .findAny().orElseThrow(() -> TraderException.NotFoundItemException.fromItemId(itemId.toHexString()));
    }
}
