package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.ResourceLevel;
import pl.app.building.buildings.application.domain.building_level.TavernLevel;
import pl.app.building.buildings.application.domain.building_level.WallLevel;

import java.time.Duration;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class WallBuilding extends Building {
    private Integer defenceIncrease;

    public WallBuilding(Integer level, BuildingType type, Integer defenceIncrease) {
        super(level, type);
        this.defenceIncrease = defenceIncrease;
    }
    public void levelUp(WallLevel buildingLevel) {
        super.levelUp(buildingLevel);
        defenceIncrease = buildingLevel.getDefenceIncrease();
    }

    public void levelDown(WallLevel buildingLevel) {
        super.levelDown(buildingLevel);
        defenceIncrease = buildingLevel.getDefenceIncrease();
    }
}
