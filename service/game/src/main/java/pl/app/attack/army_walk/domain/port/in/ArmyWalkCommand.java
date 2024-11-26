package pl.app.attack.army_walk.domain.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.attack.army_walk.domain.application.ArmyWalkType;
import pl.app.inventory.shared.Officers;
import pl.app.resource.share.Resource;
import pl.app.unit.unit.application.domain.Army;

import java.io.Serializable;

public interface ArmyWalkCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SendArmyCommand implements Serializable {
        private ArmyWalkType type;
        private ObjectId fromVillageId;
        private ObjectId toVillageId;
        private Army army;
        private Resource resource;
        private Officers officers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ProcessArmyArrivalCommand implements Serializable {
        private ObjectId walkId;
    }
}
