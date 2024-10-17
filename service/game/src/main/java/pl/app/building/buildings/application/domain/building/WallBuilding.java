package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.WallLevel;

import java.util.Map;

@Getter
public class WallBuilding extends Building {
    private Integer defenceIncrease;

    public WallBuilding() {
        super(0, BuildingType.WALL);
        this.defenceIncrease = 0;
    }

    public WallBuilding(Integer level, Integer defenceIncrease) {
        super(level, BuildingType.WALL);
        this.defenceIncrease = defenceIncrease;
    }

    public void levelUp(WallLevel buildingLevel) {
        super.levelUp(buildingLevel);
        defenceIncrease = buildingLevel.getDefenceIncrease();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        defenceIncrease = ((WallLevel) buildingLevel).getDefenceIncrease();
    }

    public void levelDown(WallLevel buildingLevel) {
        super.levelDown(buildingLevel);
        defenceIncrease = buildingLevel.getDefenceIncrease();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        defenceIncrease = ((WallLevel) buildingLevel).getDefenceIncrease();
    }
}
