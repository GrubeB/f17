package pl.app.attack.army_walk.domain.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.inventory.shared.Officers;
import pl.app.map.map.application.domain.Position;
import pl.app.resource.share.Resource;
import pl.app.army.unit.model.Army;
import pl.app.army.unit.model.Unit;
import pl.app.army.unit.model.UnitType;

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
    private Officers officers;
    private Boolean processed;

    public ArmyWalk() {
    }

    public ArmyWalk(ArmyWalkType type,
                    Map<UnitType, Unit> units,
                    ArmyWalkVillage from,
                    ArmyWalkVillage to,
                    Army army,
                    Resource resource,
                    Officers officers) {
        this.armyWalkId = ObjectId.get();
        this.type = type;
        this.officers = officers;
        this.from = from;
        this.to = to;
        this.army = Army.of(army);
        this.armySpeed = calculateArmySpeed(type, army, units, officers.getDeceiver(), officers.getTactician());
        this.distance = Position.calculateDistance(to.getPosition(), from.getPosition());
        this.duration = calculateDuration(this.armySpeed, this.distance);
        this.startDate = Instant.now();
        this.arriveDate = this.startDate.plus(duration);
        this.resource = resource;
        this.processed = false;
    }

    private int calculateArmySpeed(ArmyWalkType type, Army army, Map<UnitType, Unit> units, Boolean deceiver, Boolean tactician) {
        if (ArmyWalkType.SUPPORT.equals(type) && tactician) {
            return units.get(UnitType.LIGHT_CAVALRY).getSpeed();
        }
        if (deceiver) {
            return units.get(UnitType.NOBLEMAN).getSpeed();
        }
        Optional<Unit> max = army.army().entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .map(units::get)
                .max(Comparator.comparing(Unit::getSpeed));
        return max.map(Unit::getSpeed).orElseThrow(RuntimeException::new);
    }

    private Duration calculateDuration(int speed, double distance) {
        return Duration.ofSeconds((int) (speed * distance));
    }


    public void markAsProcessed() {
        this.processed = true;
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
