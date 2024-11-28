package pl.app.army.recruiter.application.port.in;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.army.unit.model.UnitType;
import pl.app.building.building.model.BuildingType;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinCommand;
import pl.app.gold_coin.gold_coin.application.port.in.PlayerGoldCoinService;
import pl.app.player.player.service.PlayerService;
import pl.app.player.player.service.dto.PlayerCreateDto;
import pl.app.resource.share.Resource;
import pl.app.resource.village_resource.application.port.in.VillageResourceCommand;
import pl.app.resource.village_resource.application.port.in.VillageResourceService;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecruiterServiceTest extends AbstractIntegrationTest {

    @Autowired
    private RecruiterService service;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private VillageService villageService;
    @Autowired
    private VillageInfrastructureService villageInfrastructureService;
    @Autowired
    private VillageResourceService villageResourceService;
    @Autowired
    private PlayerGoldCoinService playerGoldCoinService;

    @Test
    void add_shouldRecruitNobleManWhenThereIsEnoughGoldCoins() {
        var player = playerService.create(PlayerCreateDto.builder().accountId(ObjectId.get().toHexString()).build()).block();
        Village village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player.getPlayerId())).block();
        villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(village.getId(), BuildingType.HEADQUARTERS, 19))
                .then(villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(village.getId(), BuildingType.ACADEMY, 1)))
                .then(villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(village.getId(), BuildingType.WAREHOUSE, 19)))
                .then(villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))))
                .then(playerGoldCoinService.mint(new PlayerGoldCoinCommand.MintGoldCoinCommand(village.getId(), 2)))
                .block();

        StepVerifier.create(service.add(new RecruiterCommand.AddRecruitRequestCommand(village.getId(), UnitType.NOBLEMAN, 1)))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getFirstRequest().isPresent()).isTrue();
                    Assertions.assertThat(next.getFirstRequest().get().getUnit().getType()).isEqualTo(UnitType.NOBLEMAN);
                })
                .verifyComplete();
    }

    @Test
    void start_shouldStart_whenThereIsRequestInQueue() {
        var player = playerService.create(PlayerCreateDto.builder().accountId(ObjectId.get().toHexString()).build()).block();
        Village village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player.getPlayerId())).block();
        service.add(new RecruiterCommand.AddRecruitRequestCommand(village.getId(), UnitType.SPEARMAN, 1)).block();

        StepVerifier.create(service.start(new RecruiterCommand.StartRecruitRequestCommand(village.getId())))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getRequests().size()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void finish_shouldFinish_whenIsAfterToDate() throws InterruptedException {
        var player = playerService.create(PlayerCreateDto.builder().accountId(ObjectId.get().toHexString()).build()).block();
        Village village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player.getPlayerId())).block();
        service.add(new RecruiterCommand.AddRecruitRequestCommand(village.getId(), UnitType.SPEARMAN, 1)).block();
        service.start(new RecruiterCommand.StartRecruitRequestCommand(village.getId())).block();
        Thread.sleep(91_000);
        StepVerifier.create(service.finish(new RecruiterCommand.FinishRecruitRequestCommand(village.getId())))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getRequests().size()).isEqualTo(0);
                })
                .verifyComplete();
    }
}