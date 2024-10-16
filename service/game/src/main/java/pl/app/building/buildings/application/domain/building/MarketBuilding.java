package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.HospitalLevel;
import pl.app.building.buildings.application.domain.building_level.MarketLevel;

@Getter
@NoArgsConstructor
public class MarketBuilding extends Building {
    private Integer traderNumber;

    public MarketBuilding(Integer level, BuildingType type, Integer traderNumber) {
        super(level, type);
        this.traderNumber = traderNumber;
    }
    public void levelUp(MarketLevel buildingLevel) {
        super.levelUp(buildingLevel);
        traderNumber = buildingLevel.getTraderNumber();
    }

    public void levelDown(MarketLevel buildingLevel) {
        super.levelDown(buildingLevel);
        traderNumber = buildingLevel.getTraderNumber();
    }
}
