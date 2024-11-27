package pl.app.village.village.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface VillageEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageCreatedEvent implements Serializable {
        private ObjectId villageId;
        private VillageType villageType;
        private ObjectId ownerId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageConqueredEvent implements Serializable {
        private ObjectId villageId;
        private ObjectId currentOwnerId;
        private ObjectId previousOwnerId;
    }
}
