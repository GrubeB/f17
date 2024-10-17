package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;

@Getter
public class ChurchBuilding extends Building {
    public ChurchBuilding() {
        super(0, BuildingType.CHURCH);
    }

    public ChurchBuilding(Integer level) {
        super(level, BuildingType.CHURCH);
    }
}
