package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.RallyPointLevel;

import java.util.Map;

@Getter
public class RallyPointBuilding extends Building {
    private Integer speedIncreaseAgainstBarbarians;

    public RallyPointBuilding() {
        super(0, BuildingType.RALLY_POINT);
        this.speedIncreaseAgainstBarbarians = 0;
    }

    public RallyPointBuilding(Integer level, Integer speedIncreaseAgainstBarbarians) {
        super(level, BuildingType.RALLY_POINT);
        this.speedIncreaseAgainstBarbarians = speedIncreaseAgainstBarbarians;
    }

    public void levelUp(RallyPointLevel buildingLevel) {
        super.levelUp(buildingLevel);
        speedIncreaseAgainstBarbarians = buildingLevel.getSpeedIncreaseAgainstBarbarians();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        speedIncreaseAgainstBarbarians = ((RallyPointLevel) buildingLevel).getSpeedIncreaseAgainstBarbarians();
    }

    public void levelDown(RallyPointLevel buildingLevel) {
        super.levelDown(buildingLevel);
        speedIncreaseAgainstBarbarians = buildingLevel.getSpeedIncreaseAgainstBarbarians();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        speedIncreaseAgainstBarbarians = ((RallyPointLevel) buildingLevel).getSpeedIncreaseAgainstBarbarians();
    }
}
