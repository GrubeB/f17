package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.RallyPointLevel;
import pl.app.building.buildings.application.domain.building_level.TavernLevel;

@Getter
@NoArgsConstructor
public class TavernBuilding extends Building {
    private Integer spyNumber;

    public TavernBuilding(Integer level, BuildingType type, Integer spyNumber) {
        super(level, type);
        this.spyNumber = spyNumber;
    }
    public void levelUp(TavernLevel buildingLevel) {
        super.levelUp(buildingLevel);
        spyNumber = buildingLevel.getSpyNumber();
    }

    public void levelDown(TavernLevel buildingLevel) {
        super.levelDown(buildingLevel);
        spyNumber = buildingLevel.getSpyNumber();
    }
}
