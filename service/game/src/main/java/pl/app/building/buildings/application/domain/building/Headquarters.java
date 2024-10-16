package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.HeadquartersLevel;

import java.time.Duration;

@Getter
@NoArgsConstructor
public class Headquarters extends Building {
    private Duration finishBuildingDuration;

    public Headquarters(Integer level, BuildingType type, Duration finishBuildingDuration) {
        super(level, type);
        this.finishBuildingDuration = finishBuildingDuration;
    }

    public void levelUp(HeadquartersLevel buildingLevel) {
        super.levelUp(buildingLevel);
        finishBuildingDuration = buildingLevel.getFinishBuildingDuration();
    }

    public void levelDown(HeadquartersLevel buildingLevel) {
        super.levelDown(buildingLevel);
        finishBuildingDuration = buildingLevel.getFinishBuildingDuration();
    }
}
