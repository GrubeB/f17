package pl.app.energy.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface EnergyCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateEnergyCommand implements Serializable {
        private ObjectId godId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RefreshEnergyCommand implements Serializable {
        private ObjectId godId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddEnergyCommand implements Serializable {
        private ObjectId godId;
        private Long amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SubtractEnergyCommand implements Serializable {
        private ObjectId godId;
        private Long amount;
    }
}
