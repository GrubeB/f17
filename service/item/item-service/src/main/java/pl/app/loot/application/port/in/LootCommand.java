package pl.app.loot.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.loot.aplication.domain.Loot;

import java.io.Serializable;


public interface LootCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateLootCommand implements Serializable {
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveLootCommand implements Serializable {
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SetItemCommand implements Serializable {
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
        private ObjectId itemTemplateId;
        private Integer chance;
        private Integer amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveItemCommand implements Serializable {
        private ObjectId domainObjectId;
        private Loot.LootDomainObjectType domainObjectType;
        private ObjectId itemTemplateId;
    }
}
