package pl.app.building.buildings.application.domain.building_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.buildings.application.domain.BuildingLevel;
import pl.app.resource.resource.application.domain.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
public class WallLevel extends BuildingLevel {
    private Integer defenceIncrease;

    public WallLevel(Integer level, Resource cost, Duration duration, Set<Requirement> requirements, Integer defenceIncrease) {
        super(level, cost, duration, requirements);
        this.defenceIncrease = defenceIncrease;
    }
}
