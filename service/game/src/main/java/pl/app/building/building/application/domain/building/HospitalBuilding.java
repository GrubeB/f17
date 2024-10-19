package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.domain.building_level.HospitalLevel;

import java.util.Map;

@Getter
public class HospitalBuilding extends Building {
    private Integer beds;

    public HospitalBuilding() {
        super(0, BuildingType.HOSPITAL);
        this.beds = 0;
    }

    public HospitalBuilding(Integer level, Integer beds) {
        super(level, BuildingType.HOSPITAL);
        this.beds = beds;
    }

    public void levelUp(HospitalLevel buildingLevel) {
        super.levelUp(buildingLevel);
        beds = buildingLevel.getBeds();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        beds = ((HospitalLevel) buildingLevel).getBeds();
    }

    public void levelDown(HospitalLevel buildingLevel) {
        super.levelDown(buildingLevel);
        beds = buildingLevel.getBeds();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        beds = ((HospitalLevel) buildingLevel).getBeds();
    }
}
