package pl.app.unit.unit.application.port.in;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.app.building.building.application.domain.BuildingType.*;
import static pl.app.unit.unit.application.domain.UnitAttackType.*;
import static pl.app.unit.unit.application.domain.UnitType.*;

@Component
@NoArgsConstructor
public class UnitDomainRepositoryImpl implements UnitDomainRepository {
    private final Map<UnitType, Unit> units = new HashMap<>() {{
        put(SPEARMAN, new Unit(SPEARMAN, new Resource(50, 30, 20, 1), INFANTRY,
                10, 25, 45, 10, 14*60, 25, Duration.ofSeconds(90), Set.of()));
        put(SWORDSMAN, new Unit(SWORDSMAN, new Resource(30, 30, 70, 1), INFANTRY,
                25, 55, 5, 30, 18 *60, 15, Duration.ofSeconds(120), Set.of(new Unit.Requirement(BARRACKS, 3))));
        put(AXE_FIGHTER, new Unit(AXE_FIGHTER, new Resource(60, 30, 40, 1), INFANTRY,
                45, 10, 5, 10, 14*60, 20, Duration.ofSeconds(120), Set.of(new Unit.Requirement(BARRACKS, 5))));
        put(ARCHER, new Unit(ARCHER, new Resource(80, 30, 60, 1), ARCHERS,
                25, 10, 30, 60, 14*60, 10, Duration.ofSeconds(180), Set.of(new Unit.Requirement(BARRACKS, 9))));
        put(LIGHT_CAVALRY, new Unit(LIGHT_CAVALRY, new Resource(125, 100, 250, 4), CAVALRY,
                130, 30, 40, 30, 8*60, 50, Duration.ofSeconds(360), Set.of(new Unit.Requirement(BARRACKS, 11))));
        put(MOUNTED_ARCHER, new Unit(MOUNTED_ARCHER, new Resource(250, 200, 100, 5), ARCHERS,
                150, 40, 30, 50, 8*60, 50, Duration.ofSeconds(420), Set.of(new Unit.Requirement(BARRACKS, 13))));
        put(HEAVY_CAVALRY, new Unit(HEAVY_CAVALRY, new Resource(200, 150, 600, 6), CAVALRY,
                150, 200, 160, 180, 9*60, 50, Duration.ofSeconds(600), Set.of(new Unit.Requirement(BARRACKS, 21))));
        put(RAM, new Unit(RAM, new Resource(300, 200, 200, 5), INFANTRY,
                2, 20, 50, 20, 24*60, 0, Duration.ofSeconds(480), Set.of(new Unit.Requirement(BARRACKS, 15))));
        put(CATAPULT, new Unit(CATAPULT, new Resource(320, 400, 100, 8), ARCHERS,
                100, 100, 50, 100, 24*60, 0, Duration.ofSeconds(1200), Set.of(new Unit.Requirement(BARRACKS, 17))));
        put(PALADIN, new Unit(PALADIN, new Resource(0, 0, 0, 1), INFANTRY,
                150, 250, 400, 150, 8*60, 100, Duration.ofHours(6), Set.of(new Unit.Requirement(STATUE, 1))));
        put(NOBLEMAN, new Unit(NOBLEMAN, new Resource(40000, 50000, 50000, 100), INFANTRY,
                30, 100, 50, 100, 35*60, 0, Duration.ofHours(3), Set.of(new Unit.Requirement(ACADEMY, 1))));
        put(BERSERKER, new Unit(BERSERKER, new Resource(1200, 1200, 2400, 8), INFANTRY,
                100, 100, 50, 100, 14*60, 0, Duration.ofSeconds(1200), Set.of(new Unit.Requirement(BARRACKS, 25)))); // TODO
        put(TREBUCHET, new Unit(TREBUCHET, new Resource(4000, 2000, 2000, 10), ARCHERS,
                30, 200, 250, 100, 50*60, 0, Duration.ofSeconds(1200), Set.of(new Unit.Requirement(BARRACKS, 25))));// TODO
    }};
    private final Map<UnitType, Unit> unitsWithIncreasedSpeed = units.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        Unit originalUnit = entry.getValue();
                        return new Unit(
                                originalUnit.getType(),
                                originalUnit.getCost(),
                                originalUnit.getAttackType(),
                                originalUnit.getAttack(),
                                originalUnit.getInfantryDef(),
                                originalUnit.getCavalryDef(),
                                originalUnit.getArcherDef(),
                                1,
                                originalUnit.getCapacity(),
                                originalUnit.getTrainingTime(),
                                originalUnit.getRequirements()
                        );
                    }
            ));
    @Override
    public Mono<Map<UnitType, Unit>> fetchAll() {
        return Mono.just(unitsWithIncreasedSpeed);
    }

    @Override
    public Mono<Unit> fetch(UnitType type) {
        return Mono.just(unitsWithIncreasedSpeed.get(type));
    }
}
