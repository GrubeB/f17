package pl.app.gold_coin.gold_coin.application.port.in;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.building.building.application.domain.BuildingType;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureCommand;
import pl.app.building.village_infrastructure.application.port.in.VillageInfrastructureService;
import pl.app.common.shared.test.AbstractIntegrationTest;
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
class PlayerGoldCoinServiceTest extends AbstractIntegrationTest {

    @Autowired
    private PlayerGoldCoinService service;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private VillageService villageService;

    @Autowired
    private VillageInfrastructureService villageInfrastructureService;
    @Autowired
    private VillageResourceService villageResourceService;

    @Test
    void crate_shouldCrate_whenCommandIsValid() {
        var playerId = ObjectId.get();
        StepVerifier.create(service.crate(new PlayerGoldCoinCommand.CreatePlayerGoldCoinCommand(playerId)))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void mint_shouldMintCoin_whenVillageMeetRequirementsAndHaveEnoughResources() {
        var player = playerService.create(PlayerCreateDto.builder().accountId(ObjectId.get().toHexString()).build()).block();
        Village village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player.getPlayerId())).block();
        villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(village.getId(), BuildingType.HEADQUARTERS, 19))
                .then(villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(village.getId(), BuildingType.ACADEMY, 1)))
                .then(villageInfrastructureService.levelUp(new VillageInfrastructureCommand.LevelUpVillageInfrastructureBuildingCommand(village.getId(), BuildingType.WAREHOUSE, 19)))
                .then(villageResourceService.add(new VillageResourceCommand.AddResourceCommand(village.getId(), Resource.of(100_000))))
                .block();
        StepVerifier.create(service.mint(new PlayerGoldCoinCommand.MintGoldCoinCommand(village.getId(), 1)))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                    Assertions.assertThat(next.getAmount()).isEqualTo(1);
                })
                .verifyComplete();

    }
}