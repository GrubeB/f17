package pl.app.god.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface GodCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGodCommand implements Serializable {
        private ObjectId accountId;
        private String name;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddMoneyCommand implements Serializable {
        private ObjectId godId;
        private Long amount;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractMoneyCommand implements Serializable {
        private ObjectId godId;
        private Long amount;
    }
}
