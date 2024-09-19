package pl.app.tower_attack.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Set;


public interface TowerAttackCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AttackTowerCommand implements Serializable {
        private ObjectId godId;
        private Set<ObjectId> characterIds;
        private Integer level;
    }
}
