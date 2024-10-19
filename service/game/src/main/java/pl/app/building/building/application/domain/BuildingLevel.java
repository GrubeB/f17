package pl.app.building.building.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.resource.resource.application.domain.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
public class BuildingLevel {
    private Integer level;
    private Resource cost;
    private Duration duration;
    private Set<Requirement> requirements;

    public BuildingLevel(Integer level, Resource cost, Duration duration, Set<Requirement> requirements) {
        this.level = level;
        this.cost = cost;
        this.duration = duration;
        this.requirements = requirements;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Requirement {
        private BuildingType buildingType;
        private Integer level;
    }
}
