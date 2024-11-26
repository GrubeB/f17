package pl.app.attack.army_walk.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.attack.army_walk.domain.application.ArmyWalkType;
import pl.app.map.map.application.domain.Position;
import pl.app.resource.share.Resource;
import pl.app.unit.unit.application.domain.Army;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

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