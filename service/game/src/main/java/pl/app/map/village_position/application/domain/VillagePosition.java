package pl.app.map.village_position.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.map.map.application.domain.Position;

@Getter
@Document(collection = "village_position")
public class VillagePosition {
    @Id
    private ObjectId villageId;
    private Position position;

    public VillagePosition() {
    }

    public VillagePosition(Position position, ObjectId villageId) {
        this.position = position;
        this.villageId = villageId;
    }
}
