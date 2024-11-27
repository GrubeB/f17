package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingType;

@Getter
public class ChapelBuilding extends Building {
    public ChapelBuilding() {
        super(0, BuildingType.CHAPEL);
    }

    public ChapelBuilding(Integer level) {
        super(level, BuildingType.CHAPEL);
    }
}
