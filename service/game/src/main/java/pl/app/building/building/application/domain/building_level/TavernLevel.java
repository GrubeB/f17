package pl.app.building.building.application.domain.building_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.resource.share.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
public class TavernLevel extends BuildingLevel {
    private Integer spyNumber;

    public TavernLevel(Integer level, Resource cost, Duration duration, Set<Requirement> requirements, Integer spyNumber) {
        super(level, cost, duration, requirements);
        this.spyNumber = spyNumber;
    }
}
