package pl.app.god_template.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.common.shared.model.Money;

import java.io.Serializable;

public interface GodTemplateEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodTemplateCreatedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodTemplateUpdatedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodTemplateDeletedEvent implements Serializable {
        private ObjectId id;
    }
}
