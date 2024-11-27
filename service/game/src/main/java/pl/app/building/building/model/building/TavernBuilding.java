package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.TavernLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        spyNumber = ((TavernLevel) buildingLevel).getSpyNumber();
    }

    public void levelDown(TavernLevel buildingLevel) {
        super.levelDown(buildingLevel);
        spyNumber = buildingLevel.getSpyNumber();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        spyNumber = ((TavernLevel) buildingLevel).getSpyNumber();
    }
}
