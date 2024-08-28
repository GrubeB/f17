package pl.app.trader.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.ItemType;

import java.io.Serializable;


public interface TraderCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CrateTraderCommand implements Serializable {
        private ObjectId godId;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RenewItemsCommand implements Serializable {
        private ObjectId godId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class BuyItemCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SellItemCommand implements Serializable {
        private ObjectId godId;
        private ObjectId itemId;
    }
}
