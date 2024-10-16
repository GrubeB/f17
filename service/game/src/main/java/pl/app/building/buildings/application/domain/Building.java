package pl.app.building.buildings.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    private Integer level;
    private BuildingType type;

    public void levelUp(BuildingLevel buildingLevel) {
        if (buildingLevel.getLevel() != level + 1) {
            throw new BuildingException.InvalidBuildingLevelException();
        }
        level = buildingLevel.getLevel();
    }
    public void levelDown(BuildingLevel buildingLevel) {
        if (buildingLevel.getLevel() != level - 1) {
            throw new BuildingException.InvalidBuildingLevelException();
        }
        level = buildingLevel.getLevel();
    }
}
