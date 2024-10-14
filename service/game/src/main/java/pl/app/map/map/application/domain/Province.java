package pl.app.map.map.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class Province {
    private ObjectId id;
    private String name;

    public Province() {
    }

    public Province(String name) {
        this.id = ObjectId.get();
        this.name = name;
    }
}
