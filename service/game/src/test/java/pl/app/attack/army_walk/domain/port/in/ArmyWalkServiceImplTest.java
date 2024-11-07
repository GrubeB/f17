package pl.app.attack.army_walk.domain.port.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.attack.army_walk.domain.application.ArmyWalk;
import pl.app.attack.army_walk.domain.application.ArmyWalkType;
import pl.app.item.inventory.application.port.in.InventoryCommand;
import pl.app.item.inventory.application.port.in.InventoryService;
import pl.app.item.item.application.domain.OfficerItem;
import pl.app.item.item.application.domain.OfficerType;
import pl.app.item.item.application.domain.Officers;
import pl.app.player.player.application.port.in.PlayerCommand;
import pl.app.player.player.application.port.in.PlayerService;
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
class ArmyWalkServiceImplTest {//extends AbstractIntegrationTest {

    @Autowired
    private ArmyWalkServiceImpl service;
    @Autowired
    private VillageService villageService;
    @Autowired
    private VillageArmyService villageArmyService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private InventoryService inventoryService;

    @Test
    void sendArmy() {
        var village1 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        var village2 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village1.getId(), Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();

        StepVerifier.create(service.sendArmy(new ArmyWalkCommand.SendArmyCommand(
                        ArmyWalkType.ATTACK,
                        village1.getId(),
                        village2.getId(),
                        Army.of(Map.of(UnitType.SPEARMAN, 100)),
                        Resource.zero(),
                        new Officers()
                )))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void sendArmy_shouldThrow_whenThereIsNotEnoughOfficers() {
        var player1 = playerService.crate(new PlayerCommand.CreatePlayerCommand(ObjectId.get().toHexString(), "Kot")).block();
        var village1 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player1.getPlayerId())).block();
        var village2 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player1.getPlayerId())).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village1.getId(), Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();

        StepVerifier.create(service.sendArmy(new ArmyWalkCommand.SendArmyCommand(ArmyWalkType.ATTACK, village1.getId(), village2.getId(),
                        Army.of(Map.of(UnitType.SPEARMAN, 100)), Resource.zero(),
                        new Officers(true, false, false, false, false, false)
                )))
                .verifyError();
    }

    @Test
    void sendArmy_shouldSendArmyAndRemoveItem_whenThereOfficers() {
        var player1 = playerService.crate(new PlayerCommand.CreatePlayerCommand(ObjectId.get().toHexString(), "Kot")).block();
        var village1 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player1.getPlayerId())).block();
        var village2 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player1.getPlayerId())).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village1.getId(), Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();
        inventoryService.add(new InventoryCommand.AddItemCommand(player1.getPlayerId(), new OfficerItem(OfficerType.GRANDMASTER), 1)).block();

        StepVerifier.create(service.sendArmy(new ArmyWalkCommand.SendArmyCommand(ArmyWalkType.ATTACK, village1.getId(), village2.getId(),
                        Army.of(Map.of(UnitType.SPEARMAN, 100)), Resource.zero(),
                        new Officers(true, false, false, false, false, false)
                )))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void processAttack() {
        var playerId = ObjectId.get();
        var village1 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(playerId)).block();
        var village2 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(playerId)).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village1.getId(), Army.of(Map.of(UnitType.SPEARMAN, 100)))).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village2.getId(), Army.of(Map.of(UnitType.SPEARMAN, 10)))).block();
        ArmyWalk armyWalk = service.sendArmy(new ArmyWalkCommand.SendArmyCommand(
                ArmyWalkType.ATTACK, village1.getId(), village2.getId(),
                Army.of(Map.of(UnitType.SPEARMAN, 100)), Resource.zero(), new Officers()
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