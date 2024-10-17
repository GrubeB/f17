package pl.app.building.village_infrastructure.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.building.buildings.application.domain.BuildingType;

import java.io.Serializable;
import java.time.Duration;

public interface VillageInfrastructureEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageInfrastructureCreatedEvent implements Serializable {
        private ObjectId villageId;
    }

    // BUILDING LEVEL UP
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer numberOfLevels;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureResourceBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer currentProduction;

        public VillageInfrastructureResourceBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentProduction) {
            super(villageId, type, numberOfLevels);
            this.currentProduction = currentProduction;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureFarmBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer provisionsUp;

        public VillageInfrastructureFarmBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer provisionsUp) {
            super(villageId, type, numberOfLevels);
            this.provisionsUp = provisionsUp;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureHeadquartersBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Duration currentFinishBuildingDuration;

        public VillageInfrastructureHeadquartersBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Duration currentFinishBuildingDuration) {
            super(villageId, type, numberOfLevels);
            this.currentFinishBuildingDuration = currentFinishBuildingDuration;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureHospitalBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer currentBeds;

        public VillageInfrastructureHospitalBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentBeds) {
            super(villageId, type, numberOfLevels);
            this.currentBeds = currentBeds;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureMarketBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer currentTraderNumber;

        public VillageInfrastructureMarketBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentTraderNumber) {
            super(villageId, type, numberOfLevels);
            this.currentTraderNumber = currentTraderNumber;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureRallyPointBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer currentSpeedIncreaseAgainstBarbarians;

        public VillageInfrastructureRallyPointBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentSpeedIncreaseAgainstBarbarians) {
            super(villageId, type, numberOfLevels);
            this.currentSpeedIncreaseAgainstBarbarians = currentSpeedIncreaseAgainstBarbarians;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureTavernBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer currentSpyNumber;

        public VillageInfrastructureTavernBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentSpyNumber) {
            super(villageId, type, numberOfLevels);
            this.currentSpyNumber = currentSpyNumber;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureWallBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer currentDefenceIncrease;

        public VillageInfrastructureWallBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentDefenceIncrease) {
            super(villageId, type, numberOfLevels);
            this.currentDefenceIncrease = currentDefenceIncrease;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureWarehouseBuildingLevelUpEvent
            extends VillageInfrastructureBuildingLevelUpEvent implements Serializable {
        private Integer currentCapacity;

        public VillageInfrastructureWarehouseBuildingLevelUpEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentCapacity) {
            super(villageId, type, numberOfLevels);
            this.currentCapacity = currentCapacity;
        }
    }

    // BUILDING LEVEL DOWN
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private ObjectId villageId;
        private BuildingType type;
        private Integer numberOfLevels;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureResourceBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer currentProduction;

        public VillageInfrastructureResourceBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentProduction) {
            super(villageId, type, numberOfLevels);
            this.currentProduction = currentProduction;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureFarmBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer provisionsDown;

        public VillageInfrastructureFarmBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer provisionsDown) {
            super(villageId, type, numberOfLevels);
            this.provisionsDown = provisionsDown;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureHeadquartersBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Duration currentFinishBuildingDuration;

        public VillageInfrastructureHeadquartersBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Duration currentFinishBuildingDuration) {
            super(villageId, type, numberOfLevels);
            this.currentFinishBuildingDuration = currentFinishBuildingDuration;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureHospitalBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer currentBeds;

        public VillageInfrastructureHospitalBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentBeds) {
            super(villageId, type, numberOfLevels);
            this.currentBeds = currentBeds;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureMarketBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer currentTraderNumber;

        public VillageInfrastructureMarketBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentTraderNumber) {
            super(villageId, type, numberOfLevels);
            this.currentTraderNumber = currentTraderNumber;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureRallyPointBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer currentSpeedIncreaseAgainstBarbarians;

        public VillageInfrastructureRallyPointBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentSpeedIncreaseAgainstBarbarians) {
            super(villageId, type, numberOfLevels);
            this.currentSpeedIncreaseAgainstBarbarians = currentSpeedIncreaseAgainstBarbarians;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureTavernBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer currentSpyNumber;

        public VillageInfrastructureTavernBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentSpyNumber) {
            super(villageId, type, numberOfLevels);
            this.currentSpyNumber = currentSpyNumber;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureWallBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer currentDefenceIncrease;

        public VillageInfrastructureWallBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentDefenceIncrease) {
            super(villageId, type, numberOfLevels);
            this.currentDefenceIncrease = currentDefenceIncrease;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    class VillageInfrastructureWarehouseBuildingLevelDownEvent
            extends VillageInfrastructureBuildingLevelDownEvent implements Serializable {
        private Integer currentCapacity;

        public VillageInfrastructureWarehouseBuildingLevelDownEvent(ObjectId villageId, BuildingType type, Integer numberOfLevels, Integer currentCapacity) {
            super(villageId, type, numberOfLevels);
            this.currentCapacity = currentCapacity;
        }
    }
}
