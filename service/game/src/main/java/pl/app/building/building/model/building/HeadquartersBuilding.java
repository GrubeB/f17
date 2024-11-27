package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.HeadquartersLevel;

import java.time.Duration;
import java.util.Map;

@Getter
public class HeadquartersBuilding extends Building {
    private Duration finishBuildingDuration;

    public HeadquartersBuilding() {
        super(0, BuildingType.HEADQUARTERS);
        this.finishBuildingDuration = Duration.ZERO;
    }

    public HeadquartersBuilding(Integer level, Duration finishBuildingDuration) {
        super(level, BuildingType.HEADQUARTERS);
        this.finishBuildingDuration = finishBuildingDuration;
    }

    public void levelUp(HeadquartersLevel buildingLevel) {
        super.levelUp(buildingLevel);
        finishBuildingDuration = buildingLevel.getFinishBuildingDuration();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        finishBuildingDuration = ((HeadquartersLevel) buildingLevel).getFinishBuildingDuration();
    }

    public void levelDown(HeadquartersLevel buildingLevel) {
        super.levelDown(buildingLevel);
        finishBuildingDuration = buildingLevel.getFinishBuildingDuration();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        finishBuildingDuration = ((HeadquartersLevel) buildingLevel).getFinishBuildingDuration();
    }
}
