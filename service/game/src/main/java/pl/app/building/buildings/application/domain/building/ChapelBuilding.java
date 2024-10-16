package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;

@Getter
@NoArgsConstructor
public class ChapelBuilding extends Building {

    public ChapelBuilding(Integer level, BuildingType type) {
        super(level, type);
    }
}
