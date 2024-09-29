package pl.app.energy.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface EnergyEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class EnergyCreatedEvent implements Serializable {
        private ObjectId id;
        private ObjectId godId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class EnergyAddedEvent implements Serializable {
        private ObjectId godId;
        private Long amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class EnergySubtractedEvent implements Serializable {
        private ObjectId godId;
        private Long amount;
    }
}
