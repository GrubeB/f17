package pl.app.monster_gear.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.gear.aplication.domain.GearSlot;

import java.io.Serializable;

public interface MonsterGearEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterGearCreatedEvent implements Serializable {
        private ObjectId id;
        private ObjectId monsterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterGearRemovedEvent implements Serializable {
        private ObjectId id;
        private ObjectId monsterId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterItemSetEvent implements Serializable {
        private ObjectId id;
        private ObjectId monsterId;
        private ObjectId itemId;
        private GearSlot slot;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    class MonsterItemRemovedEvent implements Serializable {
        private ObjectId id;
        private ObjectId monsterId;
        private ObjectId itemId;
        private GearSlot slot;
    }
}
