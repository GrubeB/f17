package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.HeadquartersLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        finishBuildingDuration = ((HeadquartersLevel) buildingLevel).getFinishBuildingDuration();
    }

    public void levelDown(HeadquartersLevel buildingLevel) {
        super.levelDown(buildingLevel);
        finishBuildingDuration = buildingLevel.getFinishBuildingDuration();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        finishBuildingDuration = ((HeadquartersLevel) buildingLevel).getFinishBuildingDuration();
    }
}
