package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingType;

@Getter
public class BarracksBuilding extends Building {
    public BarracksBuilding() {
        super(0, BuildingType.BARRACKS);
    }

    public BarracksBuilding(Integer level) {
        super(level, BuildingType.BARRACKS);
    }
}
