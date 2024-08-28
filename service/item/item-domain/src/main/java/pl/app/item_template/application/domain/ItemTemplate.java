package pl.app.item_template.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class ItemTemplate {
    @Id
    private ObjectId id;
    private ItemType type;
    private String name;
    private String description;
    private String imageId;

    @SuppressWarnings("unused")
    public ItemTemplate() {
    }

    public ItemTemplate(String typeName, String name, String description, String imageId) {
        this.id = ObjectId.get();
        this.type = ItemType.valueOf(typeName);
        this.name = name;
        this.description = description;
        this.imageId = imageId;
    }

    public void setType(String typeName) {
        this.type = ItemType.valueOf(typeName);
    }
}
