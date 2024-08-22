package pl.app.god.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface GodEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class GodCreatedEvent implements Serializable {
        private ObjectId itemId;
    }
}
