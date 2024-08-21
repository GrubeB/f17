package pl.app.item.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.item_template.application.domain.ItemTemplate;
import pl.app.item_template.application.domain.ItemType;
import pl.app.item_template.application.domain.OutfitTemplate;

@Getter
public class Item {
    @Id
    protected ObjectId id;
    @DBRef
    private ItemTemplate template;

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
}
