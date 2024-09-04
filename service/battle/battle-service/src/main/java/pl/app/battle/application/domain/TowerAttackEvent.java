package pl.app.battle.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface TowerAttackEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class TowerAttackEndedEvent implements Serializable {
        private ObjectId towerAttackId;
    }
}
