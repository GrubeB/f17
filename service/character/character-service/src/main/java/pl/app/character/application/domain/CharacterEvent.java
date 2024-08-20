package pl.app.character.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.UUID;

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
    class StatisticAddedEvent implements Serializable {
        private ObjectId characterId;
        private String statisticName;
        private Long statisticQuantity;
    }
}
