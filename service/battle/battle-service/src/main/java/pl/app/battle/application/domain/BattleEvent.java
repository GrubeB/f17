package pl.app.battle.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface BattleEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class BattleEndedEvent implements Serializable {
        private ObjectId battleId;
        private List<CharacterResult> characterResults;
    }
}
