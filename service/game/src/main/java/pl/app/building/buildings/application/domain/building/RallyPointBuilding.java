package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.RallyPointLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        speedIncreaseAgainstBarbarians = ((RallyPointLevel) buildingLevel).getSpeedIncreaseAgainstBarbarians();
    }

    public void levelDown(RallyPointLevel buildingLevel) {
        super.levelDown(buildingLevel);
        speedIncreaseAgainstBarbarians = buildingLevel.getSpeedIncreaseAgainstBarbarians();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        speedIncreaseAgainstBarbarians = ((RallyPointLevel) buildingLevel).getSpeedIncreaseAgainstBarbarians();
    }
}
