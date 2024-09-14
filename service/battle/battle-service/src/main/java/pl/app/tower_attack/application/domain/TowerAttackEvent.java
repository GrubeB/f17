package pl.app.tower_attack.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import pl.app.battle.application.domain.CharacterResult;

import java.io.Serializable;
import java.util.List;

public interface TowerAttackEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class TowerAttackStartedEvent implements Serializable {
        private ObjectId towerAttackId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class TowerAttackEndedEvent implements Serializable {
        private ObjectId towerAttackId;
        private List<CharacterResult> characterResults;
    }
}
