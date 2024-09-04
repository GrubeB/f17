package pl.app.battle.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface InnerTowerAttackEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class AttackStartedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class AttackEndedEvent implements Serializable {
        private Boolean isWin;
        private Integer numberOfSeconds;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class NewBattleStartedEvent implements Serializable {
        private ObjectId battleId;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class BattleEndedEvent implements Serializable {
        private Boolean isWin;
        private Integer numberOfSeconds;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class TeamWalkedEvent implements Serializable {
        private Integer numberOfSeconds;
    }
}
