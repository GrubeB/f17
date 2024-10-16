package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.MarketLevel;
import pl.app.building.buildings.application.domain.building_level.RallyPointLevel;

@Getter
@NoArgsConstructor
public class RallyPointBuilding extends Building {
    private Integer speedIncreaseAgainstBarbarians;

    public RallyPointBuilding(Integer level, BuildingType type, Integer speedIncreaseAgainstBarbarians) {
        super(level, type);
        this.speedIncreaseAgainstBarbarians = speedIncreaseAgainstBarbarians;
    }
    public void levelUp(RallyPointLevel buildingLevel) {
        super.levelUp(buildingLevel);
        speedIncreaseAgainstBarbarians = buildingLevel.getSpeedIncreaseAgainstBarbarians();
    }

    public void levelDown(RallyPointLevel buildingLevel) {
        super.levelDown(buildingLevel);
        speedIncreaseAgainstBarbarians = buildingLevel.getSpeedIncreaseAgainstBarbarians();
    }
}
