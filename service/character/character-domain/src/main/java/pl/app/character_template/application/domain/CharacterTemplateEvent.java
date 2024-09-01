package pl.app.character_template.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface CharacterTemplateEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterTemplateCreatedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterTemplateUpdatedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class CharacterTemplateDeletedEvent implements Serializable {
        private ObjectId id;
    }
}
