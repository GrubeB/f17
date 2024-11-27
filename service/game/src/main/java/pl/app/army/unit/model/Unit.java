package pl.app.army.unit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.app.building.building.model.BuildingType;
import pl.app.resource.share.Resource;

import java.time.Duration;
import java.util.Set;

@Getter
@Setter
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
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Requirement {
        private BuildingType buildingType;
        private Integer level;
    }
}
