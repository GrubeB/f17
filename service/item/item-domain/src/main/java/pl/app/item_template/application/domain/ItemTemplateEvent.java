package pl.app.item_template.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface ItemTemplateEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class ItemTemplateCreatedEvent implements Serializable {
        private ObjectId itemId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class OutfitTemplateCreatedEvent implements Serializable {
        private ObjectId itemId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class WeaponTemplateCreatedEvent implements Serializable {
        private ObjectId itemId;
    }
}
