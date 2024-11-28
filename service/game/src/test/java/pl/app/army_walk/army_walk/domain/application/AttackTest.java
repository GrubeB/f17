package pl.app.army_walk.army_walk.domain.application;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import pl.app.army.unit.model.Army;
import pl.app.army.unit.model.Unit;
import pl.app.army.unit.model.UnitType;
import pl.app.army.unit.service.UnitServiceImpl;
import pl.app.inventory.shared.Officers;
import pl.app.map.map.application.domain.Position;
import pl.app.map.map.application.domain.Province;
import pl.app.resource.share.Resource;

import java.util.Map;


class AttackTest {

    private final Map<UnitType, Unit> units = new UnitServiceImpl().fetchAll().block();

    @Test
    void calculatePlunderedResource() {
        var armyWalk = new ArmyWalk(
                ArmyWalkType.ATTACK,
                units,
                new ArmyWalk.ArmyWalkVillage(ObjectId.get(), ObjectId.get(), new Position(1, 1, new Province("Province"))),
                new ArmyWalk.ArmyWalkVillage(ObjectId.get(), ObjectId.get(), new Position(2, 2, new Province("Province"))),
                Army.of(Map.of(UnitType.SPEARMAN, 36)),
                Resource.zero(),
                new Officers()
        );
        var domain = new Attack(armyWalk, Army.zero(), units,
                new Attack.DefenderVillage(Resource.of(300, 150, 900, 0), 0, 100),
                true, true
        );
        Assertions.assertThat(domain.getPlunderedResource().getWood()).isEqualTo(300);
        Assertions.assertThat(domain.getPlunderedResource().getClay()).isEqualTo(150);
        Assertions.assertThat(domain.getPlunderedResource().getIron()).isEqualTo(450);
    }
}