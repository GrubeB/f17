package pl.app.building.building.model.building_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.model.BuildingLevel;
import pl.app.resource.share.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
public class FarmLevel extends BuildingLevel {
    private Integer provisions;

    public FarmLevel(Integer level, Resource cost, Duration duration, Set<Requirement> requirements, Integer provisions) {
        super(level, cost, duration, requirements);
        this.provisions = provisions;
    }
}
