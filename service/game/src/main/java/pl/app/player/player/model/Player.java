package pl.app.player.player.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "player")
public class Player {
    @Id
    private ObjectId playerId;
    private String accountId;
    private String name;
    private String description;
}
