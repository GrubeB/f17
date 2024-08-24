package pl.app.battle.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Set;


public interface BattleCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class StartDuelBattleCommand implements Serializable {
        private ObjectId god1;
        private ObjectId characterId1;
        private ObjectId god2;
        private ObjectId characterId2;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class StartTwoGodBattleCommand implements Serializable {
        private ObjectId god1;
        private Set<ObjectId> god1CharacterIds;
        private ObjectId god2;
        private Set<ObjectId> god2CharacterIds;
    }
}
