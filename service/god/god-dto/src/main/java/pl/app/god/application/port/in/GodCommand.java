package pl.app.god.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;


public interface GodCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGodCommand implements Serializable {
        private ObjectId accountId;
        private ObjectId godTemplateId;
        private String name;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddMoneyCommand implements Serializable {
        private ObjectId godId;
        private Money money;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractMoneyCommand implements Serializable {
        private ObjectId godId;
        private Money money;
    }
}
