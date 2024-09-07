package pl.app.tower.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface TowerEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class TowerLevelCreatedEvent implements Serializable {
        private ObjectId id;
        private Integer level;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class TowerLevelRemovedEvent implements Serializable {
        private ObjectId id;
        private Integer level;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class TowerLevelUpdatedEvent implements Serializable {
        private ObjectId id;
        private Integer level;
    }
}
