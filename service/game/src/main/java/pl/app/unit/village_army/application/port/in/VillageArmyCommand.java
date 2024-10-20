package pl.app.unit.village_army.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.unit.unit.application.domain.UnitType;

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
        private UnitType type;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractUnitsCommand implements Serializable {
        private ObjectId villageId;
        private UnitType type;
        private Integer amount;
    }
}
