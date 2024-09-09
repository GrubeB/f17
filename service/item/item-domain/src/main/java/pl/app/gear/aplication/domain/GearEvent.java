package pl.app.gear.aplication.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface GearEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class GearCreatedEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class GearRemovedEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class ItemSetEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
        private ObjectId itemId;
        private GearSlot slot;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class ItemRemovedEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
        private ObjectId itemId;
        private GearSlot slot;
    }
}
