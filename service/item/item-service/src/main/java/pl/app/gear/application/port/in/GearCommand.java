package pl.app.gear.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.gear.aplication.domain.Gear;
import pl.app.gear.aplication.domain.GearSlot;

import java.io.Serializable;


public interface GearCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateGearCommand implements Serializable {
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveGearCommand implements Serializable {
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SetItemCommand implements Serializable {
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
        private GearSlot slot;
        private ObjectId itemId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveItemCommand implements Serializable {
        private ObjectId domainObjectId;
        private Gear.LootDomainObjectType domainObjectType;
        private GearSlot slot;
    }
}
