package pl.app.army.recruiter.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.army.unit.model.UnitType;

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
    class RecruitRequestStartedEvent implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
        private Instant from;
        private Instant to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruitRequestFinishedEvent implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
        private Instant from;
        private Instant to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruitRequestCanceledEvent implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
        private Instant from;
        private Instant to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RecruitRequestRejectedEvent implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
        private Instant from;
        private Instant to;
    }
}
