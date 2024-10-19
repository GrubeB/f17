package pl.app.building.building.application.domain.building_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.resource.resource.application.domain.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
public class RallyPointLevel extends BuildingLevel {
    private Integer speedIncreaseAgainstBarbarians;

    public RallyPointLevel(Integer level, Resource cost, Duration duration, Set<Requirement> requirements, Integer speedIncreaseAgainstBarbarians) {
        super(level, cost, duration, requirements);
        this.speedIncreaseAgainstBarbarians = speedIncreaseAgainstBarbarians;
    }
}
