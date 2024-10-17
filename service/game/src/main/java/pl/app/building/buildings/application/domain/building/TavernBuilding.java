package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.TavernLevel;

import java.util.Map;

@Getter
public class TavernBuilding extends Building {
    private Integer spyNumber;

    public TavernBuilding() {
        super(0, BuildingType.TAVERN);
        this.spyNumber = 0;
    }

    public TavernBuilding(Integer level, Integer spyNumber) {
        super(level, BuildingType.TAVERN);
        this.spyNumber = spyNumber;
    }

    public void levelUp(TavernLevel buildingLevel) {
        super.levelUp(buildingLevel);
        spyNumber = buildingLevel.getSpyNumber();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        spyNumber = ((TavernLevel) buildingLevel).getSpyNumber();
    }

    public void levelDown(TavernLevel buildingLevel) {
        super.levelDown(buildingLevel);
        spyNumber = buildingLevel.getSpyNumber();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        spyNumber = ((TavernLevel) buildingLevel).getSpyNumber();
    }
}
