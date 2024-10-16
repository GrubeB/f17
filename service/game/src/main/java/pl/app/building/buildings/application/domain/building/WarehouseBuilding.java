package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.WarehouseLevel;

@Getter
@NoArgsConstructor
public class WarehouseBuilding extends Building {
    private Integer capacity;

    public WarehouseBuilding(Integer level, BuildingType type, Integer capacity) {
        super(level, type);
        this.capacity = capacity;
    }
    public void levelUp(WarehouseLevel buildingLevel) {
        super.levelUp(buildingLevel);
        capacity = buildingLevel.getCapacity();
    }

    public void levelDown(WarehouseLevel buildingLevel) {
        super.levelDown(buildingLevel);
        capacity = buildingLevel.getCapacity();
    }
}
