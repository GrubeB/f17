package pl.app.unit.village_army.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.unit.unit.application.domain.Army;

import java.io.Serializable;

public interface VillageArmyCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVillageArmyCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class KillAllUnitsFromVillageCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddUnitsCommand implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractUnitsCommand implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class BlockUnitsCommand implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UnblockUnitsCommand implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddVillageSupportCommand implements Serializable {
        private ObjectId supportedVillageId;
        private ObjectId supportingVillageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractVillageSupportCommand implements Serializable {
        private ObjectId supportedVillageId;
        private ObjectId supportingVillageId;
        private Army army;
    }
}
