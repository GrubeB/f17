package pl.app.loot.aplication.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface LootEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LootCreatedEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LootRemovedEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LootItemSetEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
        private ObjectId itemTemplateId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LootItemRemovedEvent implements Serializable {
        private ObjectId id;
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
        private ObjectId itemTemplateId;
    }
}
