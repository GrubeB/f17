package pl.app.village.village.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.resource.resource.application.domain.Resource;

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
}
