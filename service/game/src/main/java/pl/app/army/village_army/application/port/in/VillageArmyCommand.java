package pl.app.army.village_army.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.army.unit.model.Army;

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
    class UnlockUnitsCommand implements Serializable {
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
    class WithdrawVillageSupportCommand implements Serializable {
        private ObjectId supportedVillageId;
        private ObjectId supportingVillageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveAllUnitsFromVillageCommand implements Serializable {
        private ObjectId villageId;
    }
}
