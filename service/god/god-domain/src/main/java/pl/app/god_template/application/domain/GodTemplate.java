package pl.app.god_template.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.god.application.domain.GodMoney;

@Getter
@Setter
@Document(collection = "god_templates")
public class GodTemplate {
    @Id
    private ObjectId id;
    private String name;
    private String description;
    private String imageId;
    @SuppressWarnings("unused")
    public GodTemplate() {
    }

    public GodTemplate( String name, String description, String imageId) {
        this.id = ObjectId.get();
        this.description = description;
        this.name = name;
        this.imageId = imageId;
    }
}
