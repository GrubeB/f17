package pl.app.village.village_effect.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.village.village_effect.application.domain.EffectType;
import pl.app.village.village_effect.application.domain.VillageEffect;

import java.io.Serializable;
import java.time.Duration;

public interface VillageEffectCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateVillageEffectCommand implements Serializable {
        private ObjectId villageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AddEffectCommand implements Serializable {
        private ObjectId villageId;
        private EffectType effectType;
        private Duration duration;
        private Integer value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveInvalidEffectsCommand implements Serializable {
        private ObjectId villageId;
    }
}
