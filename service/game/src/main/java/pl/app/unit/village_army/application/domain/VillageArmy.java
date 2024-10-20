package pl.app.unit.village_army.application.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.unit.unit.application.domain.UnitType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Document(collection = "village_army")
public class VillageArmy {
    @Id
    private ObjectId villageId;
    private Map<UnitType, Integer> army;

    public VillageArmy() {
    }

    public VillageArmy(ObjectId villageId) {
        this.villageId = villageId;
        this.army = EnumSet.allOf(UnitType.class).stream()
                .collect(Collectors.toMap(
                        type -> type,
                        type -> 0,
                        (oldValue, newValue) -> oldValue,
                        () -> new EnumMap<>(UnitType.class)
                ));
    }

    public void add(UnitType unitType, Integer amount) {
        army.compute(unitType, (k, v) -> Objects.isNull(v) ? amount : v + amount);
    }

    public void subtract(UnitType unitType, Integer amount) {
        army.compute(unitType, (k, v) -> Objects.isNull(v) ? amount : v - amount);
    }
}
