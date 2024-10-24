package pl.app.player.player.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.unit.unit.application.domain.Army;

import java.io.Serializable;

public interface PlayerEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class PlayerCreatedEvent implements Serializable {
        private ObjectId playerId;
    }
}