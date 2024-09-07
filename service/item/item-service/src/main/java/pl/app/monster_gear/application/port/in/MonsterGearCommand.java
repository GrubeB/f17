package pl.app.monster_gear.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.gear.aplication.domain.GearSlot;

import java.io.Serializable;


public interface MonsterGearCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateMonsterGearCommand implements Serializable {
        private ObjectId monsterId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveMonsterGearCommand implements Serializable {
        private ObjectId monsterId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class SetMonsterItemCommand implements Serializable {
        private ObjectId monsterId;
        private GearSlot slot;
        private ObjectId itemId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveMonsterItemCommand implements Serializable {
        private ObjectId monsterId;
        private GearSlot slot;
    }
}
