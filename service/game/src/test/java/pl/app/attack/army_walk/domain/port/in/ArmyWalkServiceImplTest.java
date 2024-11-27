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
import pl.app.inventory.inventory.application.port.in.InventoryCommand;
import pl.app.inventory.inventory.application.port.in.InventoryService;
import pl.app.inventory.shared.OfficerItem;
import pl.app.inventory.shared.OfficerType;
import pl.app.inventory.shared.Officers;
import pl.app.player.player.service.PlayerService;
import pl.app.player.player.service.dto.PlayerCreateDto;
import pl.app.resource.share.Resource;
import pl.app.army.unit.model.Army;
import pl.app.army.unit.model.UnitType;
import pl.app.army.village_army.application.port.in.VillageArmyCommand;
import pl.app.army.village_army.application.port.in.VillageArmyService;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import pl.app.village.village.query.VillageDtoQueryService;
import pl.app.village.village.query.dto.VillageDto;
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
    private VillageDtoQueryService villageDtoQueryService;
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
        var player1 = playerService.create(PlayerCreateDto.builder().accountId(ObjectId.get().toHexString()).build()).block();
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
        var player1 = playerService.create(PlayerCreateDto.builder().accountId(ObjectId.get().toHexString()).build()).block();
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

    @Test
    void sendArmy_shouldConquerVillage() {
        var playerId1 = ObjectId.get();
        var playerId2 = ObjectId.get();
        var village1 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(playerId1)).block();
        var village2 = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(playerId2)).block();
        villageArmyService.add(new VillageArmyCommand.AddUnitsCommand(village1.getId(), Army.of(Map.of(UnitType.SPEARMAN, 100, UnitType.NOBLEMAN, 5)))).block();
        ArmyWalk armyWalk1 = service.sendArmy(new ArmyWalkCommand.SendArmyCommand(ArmyWalkType.ATTACK, village1.getId(), village2.getId(), Army.of(Map.of(UnitType.SPEARMAN, 10, UnitType.NOBLEMAN, 1)), Resource.zero(), new Officers())).block();
        ArmyWalk armyWalk2 = service.sendArmy(new ArmyWalkCommand.SendArmyCommand(ArmyWalkType.ATTACK, village1.getId(), village2.getId(), Army.of(Map.of(UnitType.SPEARMAN, 10, UnitType.NOBLEMAN, 1)), Resource.zero(), new Officers())).block();
        ArmyWalk armyWalk3 = service.sendArmy(new ArmyWalkCommand.SendArmyCommand(ArmyWalkType.ATTACK, village1.getId(), village2.getId(), Army.of(Map.of(UnitType.SPEARMAN, 10, UnitType.NOBLEMAN, 1)), Resource.zero(), new Officers())).block();
        ArmyWalk armyWalk4 = service.sendArmy(new ArmyWalkCommand.SendArmyCommand(ArmyWalkType.ATTACK, village1.getId(), village2.getId(), Army.of(Map.of(UnitType.SPEARMAN, 10, UnitType.NOBLEMAN, 1)), Resource.zero(), new Officers())).block();
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1))
                        .then(service.process(new ArmyWalkCommand.ProcessArmyArrivalCommand(armyWalk1.getArmyWalkId())))
                        .then(service.process(new ArmyWalkCommand.ProcessArmyArrivalCommand(armyWalk2.getArmyWalkId())))
                        .then(service.process(new ArmyWalkCommand.ProcessArmyArrivalCommand(armyWalk3.getArmyWalkId())))
                        .then(service.process(new ArmyWalkCommand.ProcessArmyArrivalCommand(armyWalk4.getArmyWalkId())))
                        .zipWith(villageDtoQueryService.fetchById(village2.getId()))
                )
                .thenAwait(Duration.ofDays(2))
                .assertNext(t -> {
                    ArmyWalk t1 = t.getT1();
                    VillageDto t2 = t.getT2();
                })
                .verifyComplete();
    }
}