package pl.app.unit.unit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.resource.resource.application.domain.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    private UnitType type;
    private Resource cost;
    private UnitAttackType attackType;
    private Integer attack;
    private Integer infantryDef;
    private Integer cavalryDef;
    private Integer archerDef;
    private Integer speed;
    private Integer capacity;
    private Duration trainingTime;
    private Set<Requirement> requirements;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Requirement {
        private BuildingType buildingType;
        private Integer level;
    }
}
