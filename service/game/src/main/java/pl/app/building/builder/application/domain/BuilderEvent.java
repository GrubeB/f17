package pl.app.building.builder.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.building.application.domain.BuildingType;

import java.io.Serializable;
import java.time.Instant;

public interface BuilderEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class BuilderCreatedEvent implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ConstructAddedEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer toLevel;
        private Instant from;
        private Instant to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ConstructRemovedEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer toLevel;
        private Instant from;
        private Instant to;
    }
}
