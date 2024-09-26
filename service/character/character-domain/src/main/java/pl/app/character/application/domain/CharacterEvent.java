package pl.app.character.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface CharacterEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterCreatedEvent implements Serializable {
        private ObjectId characterId;
        private String name;
        private String profession;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterRemovedEvent implements Serializable {
        private ObjectId characterId;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class StatisticAddedEvent implements Serializable {
        private ObjectId characterId;
        private String statisticName;
        private Long statisticQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ExpAddedEvent implements Serializable {
        private ObjectId characterId;
        private Long amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterLevelIncreasedEvent implements Serializable {
        private ObjectId characterId;
        private Integer currentLevel;
    }
}
