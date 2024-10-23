package pl.app.unit.village_army.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.UnitType;

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
}
