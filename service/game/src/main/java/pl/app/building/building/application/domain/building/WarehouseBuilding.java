package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.domain.building_level.WarehouseLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        capacity = ((WarehouseLevel) buildingLevel).getCapacity();
    }

    public void levelDown(WarehouseLevel buildingLevel) {
        super.levelDown(buildingLevel);
        capacity = buildingLevel.getCapacity();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        capacity = ((WarehouseLevel) buildingLevel).getCapacity();
    }
}
