package pl.app.resource.village_resource.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.resource.resource.application.domain.Resource;

import java.io.Serializable;

public interface VillageResourceEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageResourceCreatedEvent implements Serializable {
        private ObjectId villageId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ResourceAddedEvent implements Serializable {
        private ObjectId villageId;
        private Resource resource;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ResourceSubtractedEvent implements Serializable {
        private ObjectId villageId;
        private Resource resource;
    }
}
