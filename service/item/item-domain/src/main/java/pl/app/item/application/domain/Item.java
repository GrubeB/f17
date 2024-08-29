package pl.app.item.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.common.shared.model.Money;
import pl.app.item_template.application.domain.ItemTemplate;
import pl.app.common.shared.model.ItemType;
import pl.app.item_template.application.domain.OutfitTemplate;

@Getter
@Document(collection = "items")
public class Item {
    @Id
    protected ObjectId id;
    @DBRef
    protected ItemTemplate template;
    @SuppressWarnings("unused")
    public Item() {
    }

    public Item(ItemTemplate template) {
        this.id = ObjectId.get();
        this.template = template;
    }

    /* GETTERS */
    public void setTemplate(OutfitTemplate template) {
        this.template = template;
    }

    public ObjectId getTemplateId() {
        return template.getId();
    }

    public ItemType getType() {
        return template.getType();
    }

    public String getName() {
        return template.getName();
    }

    public String getDescription() {
        return template.getDescription();
    }

    public String getImageId() {
        return template.getImageId();
    }
    public Money getMoney(){
        Money money = template.getMoney();
        Long percentage = template.getMoneyPercentage();
        money.addMoney(Money.Type.BASE, money.getBalance(Money.Type.BASE) * (percentage/ 100_000) );
        return money;
    }
}
