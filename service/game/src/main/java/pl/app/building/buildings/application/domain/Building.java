package pl.app.building.buildings.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    private Integer level;
    private BuildingType type;

    public void levelUp(BuildingLevel buildingLevel) {
        if (Objects.isNull(buildingLevel) || buildingLevel.getLevel() != level + 1) {
            throw new BuildingException.InvalidBuildingLevelException();
        }
        level = buildingLevel.getLevel();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        var enterLevel = level;
        for (int i = 0; i < numberOfLevels; i++) {
            var buildingLevel = buildingLevels.get(enterLevel + i + 1);
            this.levelUp(buildingLevel);
        }
    }

    public void levelDown(BuildingLevel buildingLevel) {
        if (buildingLevel.getLevel() != level - 1) {
            throw new BuildingException.InvalidBuildingLevelException();
        }
        level = buildingLevel.getLevel();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        var enterLevel = level;
        for (int i = 0; i < numberOfLevels; i++) {
            var buildingLevel = buildingLevels.get(enterLevel - i - 1);
            this.levelDown(buildingLevel);
        }
    }
}
