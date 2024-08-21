package pl.app.item.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface ItemEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class OutfitCreatedEvent implements Serializable {
        private ObjectId itemId;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class WeaponCreatedEvent implements Serializable {
        private ObjectId itemId;
    }
}
