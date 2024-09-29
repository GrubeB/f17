package pl.app.tower.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Set;


public interface TowerCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateTowerLevelCommand implements Serializable {
        private Integer level;
        private Set<ObjectId> monsterIds;
        private Integer minNumberOfMonstersInBattle;
        private Integer maxNumberOfMonstersInBattle;
        private Integer energyCost;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateTowerLevelCommand implements Serializable {
        private Integer level;
        private Set<ObjectId> monsterIds;
        private Integer minNumberOfMonstersInBattle;
        private Integer maxNumberOfMonstersInBattle;
        private Integer energyCost;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveTowerLevelCommand implements Serializable {
        private Integer level;
    }
}
