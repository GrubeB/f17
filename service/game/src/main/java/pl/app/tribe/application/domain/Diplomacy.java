package pl.app.tribe.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class Diplomacy {
    @DBRef
    private List<DiplomacyTribe> tribes;

    public Diplomacy() {
        this.tribes = new LinkedList<>();
    }

    public void add(Tribe newTribe, DiplomacyType type) {
        Optional<DiplomacyTribe> tribeOpt = getDiplomacyTribeByTribeId(newTribe.getId());
        if (tribeOpt.isEmpty()) {
            this.tribes.add(new DiplomacyTribe(newTribe, type));
        }
        tribeOpt.get().setType(type);
    }

    public void remove(Tribe newTribe) {
        Optional<DiplomacyTribe> tribeOpt = getDiplomacyTribeByTribeId(newTribe.getId());
        if (tribeOpt.isPresent()) {
            this.tribes.remove(tribeOpt.get());
        }
    }

    public Optional<DiplomacyTribe> getDiplomacyTribeByTribeId(ObjectId tribeId) {
        return this.tribes.stream().filter(t -> Objects.equals(t.getTribe().getId(), tribeId)).findAny();
    }

    @Getter
    @NoArgsConstructor
    public static class DiplomacyTribe {
        private Tribe tribe;
        @Setter
        private DiplomacyType type;

        public DiplomacyTribe(Tribe tribe, DiplomacyType type) {
            this.tribe = tribe;
            this.type = type;
        }
    }

    public enum DiplomacyType {
        ALLY,
        NON_AGGRESSION_PACK,
        ENEMY
    }
}
