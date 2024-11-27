package pl.app.building.building.model.building;

import lombok.Getter;
import pl.app.building.building.model.Building;
import pl.app.building.building.model.BuildingLevel;
import pl.app.building.building.model.BuildingType;
import pl.app.building.building.model.building_level.HospitalLevel;

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

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelUp(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        beds = ((HospitalLevel) buildingLevel).getBeds();
    }

    public void levelDown(HospitalLevel buildingLevel) {
        super.levelDown(buildingLevel);
        beds = buildingLevel.getBeds();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer toLevel) {
        super.levelDown(buildingLevels, toLevel);
        var buildingLevel = buildingLevels.get(getLevel());
        beds = ((HospitalLevel) buildingLevel).getBeds();
    }
}
