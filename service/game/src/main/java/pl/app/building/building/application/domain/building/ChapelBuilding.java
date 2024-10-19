package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingType;

@Getter
public class ChapelBuilding extends Building {
    public ChapelBuilding() {
        super(0, BuildingType.CHAPEL);
    }

    public ChapelBuilding(Integer level) {
        super(level, BuildingType.CHAPEL);
    }
}
