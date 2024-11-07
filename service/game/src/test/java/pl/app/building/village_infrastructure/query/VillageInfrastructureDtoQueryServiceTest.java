package pl.app.building.village_infrastructure.query;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.village.village.application.domain.Village;
import pl.app.village.village.application.port.in.VillageCommand;
import pl.app.village.village.application.port.in.VillageService;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageInfrastructureDtoQueryServiceTest extends AbstractIntegrationTest {

    @Autowired
    private VillageInfrastructureDtoQueryService service;
    @Autowired
    private VillageService villageService;

    @Test
    void fetchByVillageId() {
        Village village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        StepVerifier.create(service.fetchByVillageId(village.getId()))
                .assertNext(next -> {
                    Assertions.assertThat(next).isNotNull();
                })
                .verifyComplete();
    }
}