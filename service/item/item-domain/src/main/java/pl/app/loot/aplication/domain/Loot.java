package pl.app.loot.aplication.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.common.shared.model.Money;

import java.util.Optional;
import java.util.Set;

@Getter
@Document(collection = "loot")
public class Loot {
    @Id
    private ObjectId id;
    private ObjectId domainObjectId;
    private LootDomainObjectType domainObjectType;

    public enum LootDomainObjectType {
        MONSTER,
    }
    private Set<LootItem> items;
    @Setter
    private Money money;

    public Loot() {
    }

    public Loot(ObjectId domainObjectId, LootDomainObjectType domainObjectType, Set<LootItem> items, Money money) {
        this.id = ObjectId.get();
        this.domainObjectId = domainObjectId;
        this.domainObjectType = domainObjectType;
        this.items = items;
        this.money = money;
    }

    public void addItem(LootItem lootItem) {
        Optional<LootItem> lootItemOptional = getLootItemByTemplateId(lootItem.getItemTemplate().getId());
        if (lootItemOptional.isPresent()) {
            lootItemOptional.get().updateData(lootItem.getChance(), lootItem.getAmount());
        } else {
            this.items.add(lootItem);
        }
    }

    public LootItem removeItem(ObjectId templateId) {
        Optional<LootItem> lootItem = getLootItemByTemplateId(templateId);
        if (lootItem.isPresent()) {
            items.remove(lootItem.get());
            return lootItem.get();
        }
        return null;
    }

    public Optional<LootItem> getLootItemByTemplateId(ObjectId templateId) {
        return items.stream()
                .filter(e -> e.getItemTemplate().getId().equals(templateId))
                .findAny();
    }
}
