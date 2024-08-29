package pl.app.item_template.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.common.shared.model.ItemType;
import pl.app.common.shared.model.Money;

@Getter
@Setter
@Document(collection = "templates")
public class ItemTemplate {
    @Id
    private ObjectId id;
    private ItemType type;
    private String name;
    private String description;
    private String imageId;
    private Money money;
    private Long moneyPercentage;

    @SuppressWarnings("unused")
    public ItemTemplate() {
    }
    public ItemTemplate(ItemType type, String name, String description, String imageId, Money money, Long moneyPercentage) {
        this.id = ObjectId.get();
        this.type = type;
        this.name = name;
        this.description = description;
        this.imageId = imageId;
        this.money = money;
        this.moneyPercentage = moneyPercentage;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
    public void setType(String typeName) {
        this.type = ItemType.valueOf(typeName);
    }
}
