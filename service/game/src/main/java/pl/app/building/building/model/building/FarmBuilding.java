package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.FarmLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        provisions = ((FarmLevel) buildingLevel).getProvisions();
    }

    public void levelDown(FarmLevel buildingLevel) {
        super.levelDown(buildingLevel);
        provisions = buildingLevel.getProvisions();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        provisions = ((FarmLevel) buildingLevel).getProvisions();
    }
}
