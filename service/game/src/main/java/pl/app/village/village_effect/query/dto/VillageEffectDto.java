package pl.app.village.village_effect.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.village.village_effect.application.domain.EffectType;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageEffectDto implements Serializable {
    private ObjectId villageId;
    private List<EffectDto> effects;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EffectDto implements Serializable {
        private EffectType type;
        private Integer value;
        private Instant from;
        private Instant to;
    }

    public int getBuffValue(EffectType type) {
        return effects.stream()
                .filter(e -> e.getType().equals(type))
                .map(VillageEffectDto.EffectDto::getValue).findAny().orElse(0);
    }
}