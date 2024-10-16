package pl.app.building.buildings.application.domain.building;

import pl.app.building.buildings.application.domain.BuildingType;

public class IronMineBuilding extends ResourceBuilding {
    public IronMineBuilding(Integer level, BuildingType type, Integer production) {
        super(level, type, production);
    }
}
