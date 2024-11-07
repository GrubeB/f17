package pl.app.player.player.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "player")
public class Player {
    @Id
    private ObjectId playerId;
    private String accountId;

    public Player() {
    }

    public Player(String accountId) {
        this.playerId = ObjectId.get();
        this.accountId = accountId;
    }
}
