package pl.app.village.village_effect.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface VillageEffectEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class VillageEffectCreatedEvent implements Serializable {
        private ObjectId villageId;
    }
}
