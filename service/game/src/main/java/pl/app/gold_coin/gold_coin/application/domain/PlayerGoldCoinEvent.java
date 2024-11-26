package pl.app.gold_coin.gold_coin.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface PlayerGoldCoinEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class PlayerGoldCoinCreatedEvent implements Serializable {
        private ObjectId playerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MoneyAddedEvent implements Serializable {
        private ObjectId playerId;
        private Integer amount;
    }
}
