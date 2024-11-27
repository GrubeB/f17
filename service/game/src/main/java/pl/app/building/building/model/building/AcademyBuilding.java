package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingType;

@Getter
public class AcademyBuilding extends Building {

    public AcademyBuilding() {
        super(0, BuildingType.ACADEMY);
    }
}
