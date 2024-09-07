package pl.app.monster.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;


public interface MonsterCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateMonsterCommand implements Serializable {
        private ObjectId templateId;
        private Integer level;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveMonsterCommand implements Serializable {
        private ObjectId monsterId;
    }
}
