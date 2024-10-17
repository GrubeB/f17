package pl.app.building.buildings.application.domain.building;

import pl.app.building.buildings.application.domain.BuildingType;

public class ClayPitBuilding extends ResourceBuilding {
    public ClayPitBuilding() {
        super(0, BuildingType.CLAY_PIT, 0);
    }

    public ClayPitBuilding(Integer level, Integer production) {
        super(level, BuildingType.CLAY_PIT, production);
    }
}
