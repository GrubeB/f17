package pl.app.army_walk.army_walk.domain.application;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.inventory.shared.Officers;
import pl.app.map.map.application.domain.Position;
import pl.app.map.map.application.domain.Province;
import pl.app.resource.share.Resource;
import pl.app.army.unit.model.Army;
import pl.app.army.unit.model.Unit;
import pl.app.army.unit.model.UnitType;
import pl.app.army.unit.service.UnitServiceImpl;

import java.time.Duration;
import java.util.Map;

class ArmyWalkTest {
    private final Map<UnitType, Unit> units = new UnitServiceImpl().fetchAll().block();

    @Test
    void testDuration() {
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