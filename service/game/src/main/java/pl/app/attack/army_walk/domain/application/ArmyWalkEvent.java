package pl.app.attack.army_walk.domain.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface ArmyWalkEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AttackStartedEvent implements Serializable {
        private ObjectId armyWalkId;
    }

}
