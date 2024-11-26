package pl.app.player.player.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface PlayerEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class PlayerCreatedEvent implements Serializable {
        private ObjectId playerId;
    }
}
