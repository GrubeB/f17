package pl.app.building.building.application.domain.building;

import pl.app.building.building.application.domain.BuildingType;

public class IronMineBuilding extends ResourceBuilding {
    public IronMineBuilding() {
        super(0, BuildingType.IRON_MINE, 0);
    }

    public IronMineBuilding(Integer level, Integer production) {
        super(level, BuildingType.IRON_MINE, production);
    }
}
