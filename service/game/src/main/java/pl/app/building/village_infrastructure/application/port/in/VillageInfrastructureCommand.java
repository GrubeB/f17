package pl.app.building.village_infrastructure.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.buildings.application.domain.BuildingType;

import java.io.Serializable;

public interface VillageInfrastructureCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVillageInfrastructureCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LevelUpVillageInfrastructureBuildingCommand implements Serializable {
        private ObjectId villageId;
        private BuildingType buildingType;
        private Integer numberOfLevels;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LevelDownVillageInfrastructureBuildingCommand implements Serializable {
        private ObjectId villageId;
        private BuildingType buildingType;
        private Integer numberOfLevels;
    }
}
