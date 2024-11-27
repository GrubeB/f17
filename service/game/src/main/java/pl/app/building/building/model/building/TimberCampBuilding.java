package pl.app.building.building.model.building;

import pl.app.building.building.model.BuildingType;

public class TimberCampBuilding extends ResourceBuilding {
    public TimberCampBuilding() {
        super(0, BuildingType.TIMBER_CAMP, 0);
    }

    public TimberCampBuilding(Integer level, Integer production) {
        super(level, BuildingType.TIMBER_CAMP, production);
    }
}
