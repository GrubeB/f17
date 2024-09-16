package pl.app.god.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import pl.app.god_template.application.domain.GodTemplate;

@Getter
@Document(collection = "gods")
public class God {
    @Id
    private ObjectId id;
    private ObjectId accountId;
    private String name;
    @DocumentReference
    private GodTemplate template;
    @Setter
    private GodMoney money;

    @SuppressWarnings("unused")
    public God() {
    }

    public God(ObjectId accountId,GodTemplate template, String name) {
        this.id = ObjectId.get();
        this.accountId = accountId;
        this.template = template;
        this.name = name;
        this.money = new GodMoney();
    }

    public String getImageId() {
        return template.getImageId();
    }
    public String getDescription() {
        return template.getDescription();
    }
}
