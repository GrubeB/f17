package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;

@Getter
@NoArgsConstructor
public class ChurchBuilding extends Building {

    public ChurchBuilding(Integer level, BuildingType type) {
        super(level, type);
    }
}
