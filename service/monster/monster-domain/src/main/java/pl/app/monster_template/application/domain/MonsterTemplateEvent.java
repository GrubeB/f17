package pl.app.monster_template.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface MonsterTemplateEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterTemplateCreatedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterTemplateUpdatedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterTemplateDeletedEvent implements Serializable {
        private ObjectId id;
    }
}
