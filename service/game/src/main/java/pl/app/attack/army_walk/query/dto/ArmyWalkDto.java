package pl.app.attack.army_walk.query.dto;

import lombok.*;
import org.bson.types.ObjectId;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.application.ArmyWalkType;
import pl.app.map.map.application.domain.Position;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArmyWalkDto implements Serializable {
    private ObjectId armyWalkId;
    private ArmyWalkType type;
    private ArmyWalkVillageDto from;
    private ArmyWalkVillageDto to;

    private Army army;

    private Instant startDate;
    private Instant arriveDate;
    private Integer armySpeed;
    private Double distance;
    private Duration duration;

    private Resource resource;
    private Boolean processed;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArmyWalkVillageDto implements Serializable {
        private ObjectId playerId;
        private ObjectId villageId;
        private Position position;
    }
}