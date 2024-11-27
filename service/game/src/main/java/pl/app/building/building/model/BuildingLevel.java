package pl.app.building.building.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.app.resource.share.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@Setter
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
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Requirement {
        private BuildingType buildingType;
        private Integer level;
    }
}
