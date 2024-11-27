package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.WallLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        defenceIncrease = ((WallLevel) buildingLevel).getDefenceIncrease();
    }

    public void levelDown(WallLevel buildingLevel) {
        super.levelDown(buildingLevel);
        defenceIncrease = buildingLevel.getDefenceIncrease();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        defenceIncrease = ((WallLevel) buildingLevel).getDefenceIncrease();
    }
}
