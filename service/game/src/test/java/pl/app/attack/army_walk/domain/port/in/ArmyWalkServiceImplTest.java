package pl.app.attack.army_walk.domain.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.application.ArmyWalkType;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.resource.resource.application.domain.Resource;
import pl.app.unit.unit.application.domain.Army;
import pl.app.unit.unit.application.domain.UnitType;
import pl.app.unit.village_army.application.port.in.VillageArmyCommand;
import pl.app.unit.village_army.application.port.in.VillageArmyService;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArmyWalkServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private ArmyWalkServiceImpl service;
    @Autowired
    private VillageService villageService;
    @Autowired
    private VillageArmyService villageArmyService;

    @Test
    void sendArmy() {
        var playerId = ObjectId.get();
        var village1 = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(playerId)).block();
        var village2 = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(playerId)).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village1.getId(), Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();

        StepVerifier.create(service.sendArmy(new ArmyWalkCommand.SendArmyCommand(
                        ArmyWalkType.ATTACK,
                        playerId, village1.getId(),
                        playerId, village2.getId(),
                        Army.of(Map.of(UnitType.SPEARMAN, 100)),
                        Resource.zero()
                )))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void processAttack() {
        var playerId = ObjectId.get();
        var village1 = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(playerId)).block();
        var village2 = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(playerId)).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village1.getId(), Army.of(Map.of(UnitType.SPEARMAN, 10)))).block();
        ArmyWalk armyWalk = service.sendArmy(new ArmyWalkCommand.SendArmyCommand(
                ArmyWalkType.ATTACK,
                playerId, village1.getId(),
                playerId, village2.getId(),
                Army.of(Map.of(UnitType.SPEARMAN, 100)),
                Resource.zero()
        )).block();
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1))
                        .then(service.process(new ArmyWalkCommand.ProcessArmyArrivalCommand(armyWalk.getArmyWalkId()))))
                .thenAwait(Duration.ofDays(2))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                })
                .verifyComplete();
    }
}