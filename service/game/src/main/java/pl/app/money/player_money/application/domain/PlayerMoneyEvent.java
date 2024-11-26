package pl.app.money.player_money.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.money.share.Money;

import java.io.Serializable;

public interface PlayerMoneyEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class PlayerMoneyCreatedEvent implements Serializable {
        private ObjectId playerId;
        private Money money;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MoneyAddedEvent implements Serializable {
        private ObjectId playerId;
        private Money money;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MoneySubtractedEvent implements Serializable {
        private ObjectId playerId;
        private Money money;
    }
}
