package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.FarmLevel;

@Getter
@NoArgsConstructor
public class FarmBuilding extends Building {
    private Integer provisions;

    public FarmBuilding(Integer level, BuildingType type, Integer provisions) {
        super(level, type);
        this.provisions = provisions;
    }

    public void levelUp(FarmLevel buildingLevel) {
        super.levelUp(buildingLevel);
        provisions = buildingLevel.getProvisions();
    }

    public void levelDown(FarmLevel buildingLevel) {
        super.levelDown(buildingLevel);
        provisions = buildingLevel.getProvisions();
    }
}
