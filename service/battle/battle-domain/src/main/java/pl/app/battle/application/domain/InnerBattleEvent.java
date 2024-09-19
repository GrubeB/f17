package pl.app.battle.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

public interface InnerBattleEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class BattleEndedEvent implements Serializable {
        private Boolean team1Win;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class CharacterTurnCounterUpdatedEvent implements Serializable {
        private Short id;
        private Long from;
        private Long to;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class CharacterTurnStartedEvent implements Serializable {
        private Short id;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class CharacterAttackedEvent implements Serializable {
        private Short id;
        private Short id2;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class CharacterHpLostEvent implements Serializable {
        private Short id;
        private Long quantity;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class CharacterDiedEvent implements Serializable {
        private Short id;
    }
}
