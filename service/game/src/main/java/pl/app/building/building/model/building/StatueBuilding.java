package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingType;

@Getter
public class StatueBuilding extends Building {
    public StatueBuilding() {
        super(0, BuildingType.STATUE);
    }

    public StatueBuilding(Integer level) {
        super(level, BuildingType.STATUE);
    }
}
