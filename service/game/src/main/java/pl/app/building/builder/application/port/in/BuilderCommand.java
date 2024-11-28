package pl.app.building.builder.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.building.model.BuildingType;

import java.io.Serializable;

public interface BuilderCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateBuilderCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddBuildingToConstructCommand implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class StartBuildingToConstructCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class FinishBuildingConstructCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CancelBuildingConstructCommand implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer toLevel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RejectBuildingConstructCommand implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
    }
}
