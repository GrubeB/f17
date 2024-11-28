package pl.app.village.loyalty.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface VillageLoyaltyCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVillageLoyaltyCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractLoyaltyCommand implements Serializable {
        private ObjectId villageId;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RefreshLoyaltyCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ResetLoyaltyCommand implements Serializable {
        private ObjectId villageId;
    }
}
