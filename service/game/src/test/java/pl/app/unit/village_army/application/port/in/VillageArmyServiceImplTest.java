package pl.app.unit.village_army.application.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.UnitType;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageArmyServiceImplTest extends AbstractIntegrationTest {
    @Autowired
    private VillageArmyServiceImpl service;

    @Test
    void add_shouldAdd_whenArmyExists() {
        var villageId = ObjectId.get();
        service.crate(new VillageArmyCommand.CreateVillageArmyCommand(villageId)).block();

        StepVerifier.create(service.add(new VillageArmyCommand.AddUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 100)))))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getVillageArmy().get(UnitType.SPEARMAN)).isEqualTo(100);
                })
                .verifyComplete();
    }

    @Test
    void subtract_shouldSubtract_whenThereIsEnoughUnits() {
        var villageId = ObjectId.get();
        service.crate(new VillageArmyCommand.CreateVillageArmyCommand(villageId)).block();
        service.add(new VillageArmyCommand.AddUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();
        StepVerifier.create(service.subtract(new VillageArmyCommand.SubtractUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 50)))))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getVillageArmy().get(UnitType.SPEARMAN)).isEqualTo(50);
                })
                .verifyComplete();
    }

    @Test
    void block_shouldSubtractAndBlockUnits_whenThereIsEnoughUnits() {
        var villageId = ObjectId.get();
        service.crate(new VillageArmyCommand.CreateVillageArmyCommand(villageId)).block();
        service.add(new VillageArmyCommand.AddUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();
        StepVerifier.create(service.block(new VillageArmyCommand.BlockUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 80)))))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getVillageArmy().get(UnitType.SPEARMAN)).isEqualTo(20);
                    assertThat(next.getBlockedArmy().get(UnitType.SPEARMAN)).isEqualTo(80);
                })
                .verifyComplete();
    }

    @Test
    void unblock_shouldAddAndUnblockUnits_whenUnitsAreBlocked() {
        var villageId = ObjectId.get();
        service.crate(new VillageArmyCommand.CreateVillageArmyCommand(villageId)).block();
        service.add(new VillageArmyCommand.AddUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();
        service.block(new VillageArmyCommand.BlockUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();
        StepVerifier.create(service.unblock(new VillageArmyCommand.UnblockUnitsCommand(villageId, Army.of(Map.of(UnitType.SPEARMAN, 100)))))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getVillageArmy().get(UnitType.SPEARMAN)).isEqualTo(100);
                    assertThat(next.getBlockedArmy().get(UnitType.SPEARMAN)).isEqualTo(0);
                })
                .verifyComplete();
    }
}