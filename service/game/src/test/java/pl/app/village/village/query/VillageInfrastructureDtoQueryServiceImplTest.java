package pl.app.village.village.query;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.player.player.service.PlayerService;
import pl.app.player.player.service.dto.PlayerCreateDto;
import pl.app.village.loyalty.application.domain.VillageLoyalty;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageInfrastructureDtoQueryServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private VillageDtoQueryServiceImpl service;

    @Autowired
    private VillageService villageService;
    @Autowired
    private PlayerService playerService;

    @Test
    void fetchById() {
        var player = playerService.create(PlayerCreateDto.builder().accountId(ObjectId.get().toHexString()).build()).block();
        var village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(player.getPlayerId())).block();

        StepVerifier.create(service.fetchById(village.getId()))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                    assertThat(next.getLoyalty()).isEqualTo(VillageLoyalty.LOYALTY_MAX);
                }).verifyComplete();
    }

    @Test
    void fetchAll() {
        StepVerifier.create(service.fetchAll())
                .expectComplete();
    }
}