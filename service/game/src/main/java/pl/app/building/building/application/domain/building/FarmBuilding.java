package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.domain.building_level.FarmLevel;

import java.util.Map;

@Getter
public class FarmBuilding extends Building {
    private Integer provisions;

    public FarmBuilding() {
        super(0, BuildingType.FARM);
        this.provisions = 0;
    }

    public FarmBuilding(Integer level, Integer provisions) {
        super(level, BuildingType.FARM);
        this.provisions = provisions;
    }

    public void levelUp(FarmLevel buildingLevel) {
        super.levelUp(buildingLevel);
        provisions = buildingLevel.getProvisions();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        provisions = ((FarmLevel) buildingLevel).getProvisions();
    }

    public void levelDown(FarmLevel buildingLevel) {
        super.levelDown(buildingLevel);
        provisions = buildingLevel.getProvisions();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        provisions = ((FarmLevel) buildingLevel).getProvisions();
    }
}
