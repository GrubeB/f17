package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.HeadquartersLevel;
import pl.app.building.buildings.application.domain.building_level.HospitalLevel;

@Getter
@NoArgsConstructor
public class HospitalBuilding extends Building {
    private Integer beds;

    public HospitalBuilding(Integer level, BuildingType type, Integer beds) {
        super(level, type);
        this.beds = beds;
    }
    public void levelUp(HospitalLevel buildingLevel) {
        super.levelUp(buildingLevel);
        beds = buildingLevel.getBeds();
    }

    public void levelDown(HospitalLevel buildingLevel) {
        super.levelDown(buildingLevel);
        beds = buildingLevel.getBeds();
    }
}
