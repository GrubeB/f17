package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingType;

@Getter
public class ChurchBuilding extends Building {
    public ChurchBuilding() {
        super(0, BuildingType.CHURCH);
    }

    public ChurchBuilding(Integer level) {
        super(level, BuildingType.CHURCH);
    }
}
