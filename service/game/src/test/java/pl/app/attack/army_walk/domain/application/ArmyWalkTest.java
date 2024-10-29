package pl.app.attack.army_walk.domain.application;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.item.item.application.domain.Officers;
import pl.app.map.map.application.domain.Position;
import pl.app.map.map.application.domain.Province;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.Unit;
import pl.app.unit.unit.application.domain.UnitType;
import pl.app.unit.unit.application.port.in.UnitDomainRepositoryImpl;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ArmyWalkTest {
    private final Map<UnitType, Unit> units = new UnitDomainRepositoryImpl().fetchAll().block();
    @Test
    void testDuration(){
        var armyWalk = new ArmyWalk(
                ArmyWalkType.ATTACK,
                units,
                new ArmyWalk.ArmyWalkVillage(ObjectId.get(), ObjectId.get(), new Position(1, 1, new Province("Province"))),
                new ArmyWalk.ArmyWalkVillage(ObjectId.get(), ObjectId.get(), new Position(1, 2, new Province("Province"))),
                Army.of(Map.of(UnitType.SPEARMAN, 36)),
                Resource.zero(),
                new Officers()
        );
        Integer spearmanSpeed = units.get(UnitType.SPEARMAN).getSpeed();
        Assertions.assertThat(armyWalk.getDuration()).isEqualTo(Duration.ofSeconds((spearmanSpeed)));
    }

}