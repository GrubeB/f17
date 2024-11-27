package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingType;

@Getter
public class BarracksBuilding extends Building {
    public BarracksBuilding() {
        super(0, BuildingType.BARRACKS);
    }

    public BarracksBuilding(Integer level) {
        super(level, BuildingType.BARRACKS);
    }
}
