package pl.app.character_status.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface CharacterStatusEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterStatusChangedEvent implements Serializable {
        private ObjectId characterId;
        private CharacterStatusType type;
    }
}
