package pl.app.god.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;

public interface GodEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodCreatedEvent implements Serializable {
        private ObjectId godId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MoneyAddedEvent implements Serializable {
        private ObjectId godId;
        private Money.Type type;
        private Long amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MoneySubtractedEvent implements Serializable {
        private ObjectId godId;
        private Money.Type type;
        private Long amount;
    }
}
