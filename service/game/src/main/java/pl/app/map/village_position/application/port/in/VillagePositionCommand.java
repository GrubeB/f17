package pl.app.map.village_position.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface VillagePositionCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVillagePositionCommand implements Serializable {
        private ObjectId villageId;
    }
}
