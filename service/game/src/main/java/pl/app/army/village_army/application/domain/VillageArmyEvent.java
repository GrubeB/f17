package pl.app.army.village_army.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.army.unit.model.Army;

import java.io.Serializable;

public interface VillageArmyEvent {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageArmyCreatedEvent implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UnitsAddedEvent implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UnitsSubtractedEvent implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UnitsBlockedEvent implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UnitsUnlockedEvent implements Serializable {
        private ObjectId villageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageSupportAddedEvent implements Serializable {
        private ObjectId villageId;
        private ObjectId supportingVillageId;
        private Army army;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageSupportWithdrawEvent implements Serializable {
        private ObjectId villageId;
        private ObjectId supportingVillageId;
        private Army army;
    }
}
