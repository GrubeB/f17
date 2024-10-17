package pl.app.resource.village_resource.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.resource.resource.application.domain.Resource;

import java.io.Serializable;

public interface VillageResourceCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVillageResourceCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddResourceCommand implements Serializable {
        private ObjectId villageId;
        private Resource resource;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractResourceCommand implements Serializable {
        private ObjectId villageId;
        private Resource resource;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ChangeMaxResourceCommand implements Serializable {
        private ObjectId villageId;
        private Resource resourceMax;
    }
}
