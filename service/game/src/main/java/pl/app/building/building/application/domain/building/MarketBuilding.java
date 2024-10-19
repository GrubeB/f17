package pl.app.building.building.application.domain.building;

import lombok.Getter;
import pl.app.building.building.application.domain.Building;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.building.application.domain.building_level.MarketLevel;

import java.util.Map;

@Getter
public class MarketBuilding extends Building {
    private Integer traderNumber;

    public MarketBuilding() {
        super(0, BuildingType.MARKET);
        this.traderNumber = 0;
    }

    public MarketBuilding(Integer level, Integer traderNumber) {
        super(level, BuildingType.MARKET);
        this.traderNumber = traderNumber;
    }

    public void levelUp(MarketLevel buildingLevel) {
        super.levelUp(buildingLevel);
        traderNumber = buildingLevel.getTraderNumber();
    }

    public void levelUp(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelUp(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        traderNumber = ((MarketLevel) buildingLevel).getTraderNumber();
    }

    public void levelDown(MarketLevel buildingLevel) {
        super.levelDown(buildingLevel);
        traderNumber = buildingLevel.getTraderNumber();
    }

    public void levelDown(Map<Integer, ? extends BuildingLevel> buildingLevels, Integer numberOfLevels) {
        super.levelDown(buildingLevels, numberOfLevels);
        var buildingLevel = buildingLevels.get(getLevel());
        traderNumber = ((MarketLevel) buildingLevel).getTraderNumber();
    }
}
