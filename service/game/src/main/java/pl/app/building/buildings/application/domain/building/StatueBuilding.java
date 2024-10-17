package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;

@Getter
public class StatueBuilding extends Building {
    public StatueBuilding() {
        super(0, BuildingType.STATUE);
    }

    public StatueBuilding(Integer level) {
        super(level, BuildingType.STATUE);
    }
}
