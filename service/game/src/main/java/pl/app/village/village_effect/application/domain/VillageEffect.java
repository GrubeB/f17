package pl.app.village.village_effect.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Document(collection = "village_effect")
@NoArgsConstructor
public class VillageEffect {
    @Id
    private ObjectId villageId;
    private List<Effect> effects;

    public VillageEffect(ObjectId villageId) {
        this.villageId = villageId;
        this.effects = new LinkedList<>();
    }

    public void add(Effect effect) {
        this.effects.removeIf(e -> Objects.equals(e.getType(), effect.getType()));
        this.effects.add(effect);
    }

    public void removeInvalidEffect() {
        var now = Instant.now();
        this.effects.removeIf(e -> e.getTo().isBefore(now));
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Effect {
        private EffectType type;
        private Integer value;
        private Instant from;
        private Instant to;
    }
}
