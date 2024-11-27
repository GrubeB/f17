package pl.app.building.building.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.model.building_level.ResourceLevel;

import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    private Integer level;
    private BuildingType type;

    public void levelUp(BuildingLevel buildingLevel) {
        if (Objects.isNull(buildingLevel) || level > buildingLevel.getLevel()) {
            throw new BuildingException.InvalidBuildingLevelException();
        }
        level = buildingLevel.getLevel();
    }
    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        var buildingLevel = buildingLevels.get(toLevel);
        levelUp(buildingLevel);
    }
    public void levelDown(BuildingLevel buildingLevel) {
        if (Objects.isNull(buildingLevel) || level < buildingLevel.getLevel()) {
            throw new BuildingException.InvalidBuildingLevelException();
        }
        level = buildingLevel.getLevel();
    }
    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        var buildingLevel = buildingLevels.get(toLevel);
        levelDown(buildingLevel);
    }

    public boolean meetRequirements(Integer level) {
        return this.level >= level;
    }


}
