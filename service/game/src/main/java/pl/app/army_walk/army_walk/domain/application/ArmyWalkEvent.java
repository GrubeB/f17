package pl.app.army_walk.army_walk.domain.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface ArmyWalkEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ArmyWalkStarted implements Serializable {
        private ObjectId armyWalkId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ArmyWalkCancelled implements Serializable {
        private ObjectId armyWalkId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ArmyWalkEnded implements Serializable {
        private ObjectId armyWalkId;
    }
}
