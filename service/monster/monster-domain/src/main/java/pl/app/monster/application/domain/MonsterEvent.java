package pl.app.monster.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface MonsterEvent {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterCreatedEvent implements Serializable {
        private ObjectId id;
        private Integer level;
        protected ObjectId helmetTemplateId;
        protected ObjectId armorTemplateId;
        protected ObjectId glovesTemplateId;
        protected ObjectId bootsTemplateId;
        protected ObjectId beltTemplateId;
        protected ObjectId ringTemplateId;
        protected ObjectId amuletTemplateId;
        protected ObjectId talismanTemplateId;

        protected ObjectId leftHandTemplateId;
        protected ObjectId rightHandTemplateId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterRemovedEvent implements Serializable {
        private ObjectId id;
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterUpdatedEvent implements Serializable {
        private ObjectId id;
        private Integer level;
        protected ObjectId helmetTemplateId;
        protected ObjectId armorTemplateId;
        protected ObjectId glovesTemplateId;
        protected ObjectId bootsTemplateId;
        protected ObjectId beltTemplateId;
        protected ObjectId ringTemplateId;
        protected ObjectId amuletTemplateId;
        protected ObjectId talismanTemplateId;

        protected ObjectId leftHandTemplateId;
        protected ObjectId rightHandTemplateId;
    }
}
