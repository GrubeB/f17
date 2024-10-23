package pl.app.attack.army_walk.domain.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.map.map.application.domain.Position;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Getter
@Document(collection = "army_walk")
public class ArmyWalk {
    @Id
    private ObjectId armyWalkId;
    private ArmyWalkType type;
    private ArmyWalkVillage from;
    private ArmyWalkVillage to;

    private Army army;

    private Instant startDate;
    private Instant arriveDate;
    private Integer armySpeed;
    private Double distance;
    private Duration duration;

    private Resource resource;

    public ArmyWalk() {
    }

    public ArmyWalk(ArmyWalkType type,
                    Map<UnitType, Unit> units,
                    ArmyWalkVillage from,
                    ArmyWalkVillage to,
                    Army army,
                    Resource resource) {
        this.armyWalkId = ObjectId.get();
        this.type = type;
        this.from = from;
        this.to = to;
        this.army = army;
        this.armySpeed = calculateArmySpeed(army, units);
        this.distance = Position.calculateDistance(to.getPosition(), from.getPosition());
        this.duration = Duration.ofSeconds((int) (armySpeed * 60 * distance));
        this.startDate = Instant.now();
        this.arriveDate = this.startDate.plus(duration);
        this.resource = resource;
    }

    private int calculateArmySpeed(Army army, Map<UnitType, Unit> units) {
        Optional<Unit> max = army.army().entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .map(units::get)
                .max(Comparator.comparing(Unit::getSpeed));
        return max.map(Unit::getSpeed).orElseThrow(RuntimeException::new);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArmyWalkVillage {
        private ObjectId playerId;
        private ObjectId villageId;
        private Position position;
    }
}
