package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.WarehouseLevel;

import java.util.Map;

@Getter
public class WarehouseBuilding extends Building {
    private Integer capacity;

    public WarehouseBuilding() {
        super(0, BuildingType.WAREHOUSE);
        this.capacity = 0;
    }

    public WarehouseBuilding(Integer level, Integer capacity) {
        super(level, BuildingType.WAREHOUSE);
        this.capacity = capacity;
    }

    public void levelUp(WarehouseLevel buildingLevel) {
        super.levelUp(buildingLevel);
        capacity = buildingLevel.getCapacity();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        capacity = ((WarehouseLevel) buildingLevel).getCapacity();
    }

    public void levelDown(WarehouseLevel buildingLevel) {
        super.levelDown(buildingLevel);
        capacity = buildingLevel.getCapacity();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        capacity = ((WarehouseLevel) buildingLevel).getCapacity();
    }
}
