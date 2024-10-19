package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingType;

@Getter
public class AcademyBuilding extends Building {

    public AcademyBuilding() {
        super(0, BuildingType.ACADEMY);
    }
}
