package pl.app.money.player_money.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.money.share.Money;

import java.io.Serializable;

public interface PlayerMoneyCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreatePlayerMoneyCommand implements Serializable {
        private ObjectId playerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddMoneyCommand implements Serializable {
        private ObjectId playerId;
        private Money money;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractMoneyCommand implements Serializable {
        private ObjectId playerId;
        private Money money;
    }
}
