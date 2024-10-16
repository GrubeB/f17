package pl.app.building.buildings.application.domain.building;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.Building;
import pl.app.building.buildings.application.domain.BuildingType;
import pl.app.building.buildings.application.domain.building_level.ResourceLevel;

import java.time.Duration;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class ResourceBuilding extends Building {
    private Integer production;
    private Instant lastRefresh;

    public ResourceBuilding(Integer level, BuildingType type, Integer production) {
        super(level, type);
        this.production = production;
        this.lastRefresh = Instant.now();
    }

    public int refreshResource() {
        Duration timeElapsed = Duration.between(lastRefresh, Instant.now());
        double secondsElapsed = timeElapsed.toSeconds();
        double totalProduction = (double) production / 3_600 * secondsElapsed;
        lastRefresh = Instant.now();
        return (int) totalProduction;
    }

    public void levelUp(ResourceLevel buildingLevel) {
        super.levelUp(buildingLevel);
        production = buildingLevel.getProduction();
    }

    public void levelDown(ResourceLevel buildingLevel) {
        super.levelDown(buildingLevel);
        production = buildingLevel.getProduction();
    }
}
