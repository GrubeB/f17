package pl.app.village.village.query;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageInfrastructureDtoQueryServiceImplTest {

    @Autowired
    private VillageDtoQueryServiceImpl service;

    @Autowired
    private VillageService villageService;

    @Test
    void fetchById() {
        var playerId = ObjectId.get();
        var villageId = villageService.crate(new VillageCommand.CreatePlayerVillageCommand(playerId)).block().getId();

        StepVerifier.create(service.fetchById(villageId))
                .assertNext(next -> {
                    assertThat(next).isNotNull();
                }).verifyComplete();
    }

    @Test
    void fetchAll() {
        StepVerifier.create(service.fetchAll())
                .expectComplete();
    }
}