package pl.app.battle.application.domain.battle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.UUID;

public interface BattleEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class BattleEndedEvent implements Serializable {
        private ObjectId battleId;
    }
}
