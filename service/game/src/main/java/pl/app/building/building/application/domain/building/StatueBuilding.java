package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingType;

@Getter
public class StatueBuilding extends Building {
    public StatueBuilding() {
        super(0, BuildingType.STATUE);
    }

    public StatueBuilding(Integer level) {
        super(level, BuildingType.STATUE);
    }
}
