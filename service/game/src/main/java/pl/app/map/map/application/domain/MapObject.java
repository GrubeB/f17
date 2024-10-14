package pl.app.map.map.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class MapObject {
    private ObjectId id;
    private Position position;
    private MapObjectType type;

    public MapObject() {
    }

    public MapObject(Position position, MapObjectType type) {
        this.id = ObjectId.get();
        this.position = position;
        this.type = type;
    }
}
