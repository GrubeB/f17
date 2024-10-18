package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.ResourceLevel;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ResourceBuilding extends Building {
    private Integer production;

    public ResourceBuilding(Integer level, BuildingType type, Integer production) {
        super(level, type);
        this.production = production;
    }

    public void levelUp(ResourceLevel buildingLevel) {
        super.levelUp(buildingLevel);
        production = buildingLevel.getProduction();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        production = ((ResourceLevel) buildingLevel).getProduction();
    }

    public void levelDown(ResourceLevel buildingLevel) {
        super.levelDown(buildingLevel);
        production = buildingLevel.getProduction();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        production = ((ResourceLevel) buildingLevel).getProduction();
    }
}
