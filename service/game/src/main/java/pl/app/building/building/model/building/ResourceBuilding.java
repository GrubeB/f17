package pl.app.building.building.model.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.ResourceLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        production = ((ResourceLevel) buildingLevel).getProduction();
    }

    public void levelDown(ResourceLevel buildingLevel) {
        super.levelDown(buildingLevel);
        production = buildingLevel.getProduction();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        production = ((ResourceLevel) buildingLevel).getProduction();
    }
}
