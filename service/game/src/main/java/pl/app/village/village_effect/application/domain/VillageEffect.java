package pl.app.village.village_effect.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    public void start(Effect effect) {
        this.effects.removeIf(e -> Objects.equals(e.getType(), effect.getType()));
        this.effects.add(effect);
    }

    public Set<Effect> removeInvalidEffects() {
        var now = Instant.now();
        var expiredEffects = effects.stream().filter(e -> e.getTo().isBefore(now)).collect(Collectors.toSet());
        this.effects.removeAll(expiredEffects);
        return expiredEffects;
    }

    public Set<Effect> rejectAllEffects() {
        var now = Instant.now();
        var rejectedEffects = new HashSet<>(effects);
        this.effects.removeAll(rejectedEffects);
        return rejectedEffects;
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
