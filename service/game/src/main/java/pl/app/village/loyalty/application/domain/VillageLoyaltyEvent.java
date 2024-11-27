package pl.app.village.loyalty.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface VillageLoyaltyEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageLoyaltyCreatedEvent implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LoyaltyAddedEvent implements Serializable {
        private ObjectId villageId;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LoyaltySubtractedEvent implements Serializable {
        private ObjectId villageId;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LoyaltyResetEvent implements Serializable {
        private ObjectId villageId;
        private Integer amount;
    }
}
