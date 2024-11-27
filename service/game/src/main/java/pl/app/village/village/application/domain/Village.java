package pl.app.village.village.application.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Document(collection = "village")
public class Village {
    private ObjectId id;
    private VillageType type;
    private ObjectId ownerId; // playerId or null

    public Village() {
    }

    public Village(VillageType type, ObjectId ownerId) {
        this.id = ObjectId.get();
        this.type = type;
        this.ownerId = ownerId;
    }
}
