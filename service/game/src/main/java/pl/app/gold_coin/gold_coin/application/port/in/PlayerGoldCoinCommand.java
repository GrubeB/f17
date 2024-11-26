package pl.app.gold_coin.gold_coin.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface PlayerGoldCoinCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreatePlayerGoldCoinCommand implements Serializable {
        private ObjectId playerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MintGoldCoinCommand implements Serializable {
        private ObjectId villageId;
        private Integer amount;
    }
}
