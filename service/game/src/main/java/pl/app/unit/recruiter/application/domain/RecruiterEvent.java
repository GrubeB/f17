package pl.app.unit.recruiter.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.unit.unit.application.domain.UnitType;

import java.io.Serializable;
import java.time.Instant;

public interface RecruiterEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruiterCreatedEvent implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruitRequestAddedEvent implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
        private Instant from;
        private Instant to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruitRequestRemovedEvent implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
        private Instant from;
        private Instant to;
    }
}
