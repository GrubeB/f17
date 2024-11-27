package pl.app.village.village.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface VillageCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreatePlayerVillageCommand implements Serializable {
        private ObjectId playerId;
    }

    @Data
    class CreateBarbarianVillageCommand implements Serializable {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ConquerVillageCommand implements Serializable {
        private ObjectId villageId;
        private ObjectId newOwnerId;
    }
}
