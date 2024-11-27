package pl.app.building.building.model.building_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.model.BuildingLevel;
import pl.app.resource.share.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
public class ChurchLevel extends BuildingLevel {

    public ChurchLevel(Integer level, Resource cost, Duration duration, Set<Requirement> requirements) {
        super(level, cost, duration, requirements);
    }
}
