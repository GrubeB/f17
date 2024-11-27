package pl.app.building.builder.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.building.model.BuildingType;

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
    class ConstructStartedEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer toLevel;
        private Instant from;
        private Instant to;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ConstructFinishedEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer toLevel;
        private Instant from;
        private Instant to;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ConstructRejectedEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer toLevel;
        private Instant from;
        private Instant to;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ConstructCanceledEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer toLevel;
        private Instant from;
        private Instant to;
    }
}
