package pl.app.god.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "gods")
public class God {
    @Id
    private ObjectId id;
    private ObjectId accountId;
    private String name;

    @SuppressWarnings("unused")
    public God() {
    }

    public God(ObjectId accountId,String name) {
        this.id = ObjectId.get();
        this.accountId = accountId;
        this.name = name;
    }
}
