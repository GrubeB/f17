package pl.app.building.building.application.domain.building_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.application.domain.BuildingLevel;
import pl.app.resource.resource.application.domain.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
public class HospitalLevel extends BuildingLevel {
    private Integer beds;

    public HospitalLevel(Integer level, Resource cost, Duration duration, Set<Requirement> requirements, Integer beds) {
        super(level, cost, duration, requirements);
        this.beds = beds;
    }
}
