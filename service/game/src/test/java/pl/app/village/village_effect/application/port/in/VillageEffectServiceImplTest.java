package pl.app.village.village_effect.application.port.in;

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
import pl.app.village.village_effect.application.domain.EffectType;
import reactor.test.StepVerifier;

import java.time.Duration;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VillageEffectServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private VillageEffectServiceImpl service;
    @Autowired
    private VillageService villageService;

    @Test
    void add() {
        Village village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        StepVerifier.create(service.add(new VillageEffectCommand.AddEffectCommand(village.getId(), EffectType.WOOD_BUFF, Duration.ofDays(2), 20)))
                .assertNext(next -> {
                    Assertions.assertThat(next.getEffects().size()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void remove() throws InterruptedException {
        Village village = villageService.cratePlayerVillage(new VillageCommand.CreatePlayerVillageCommand(ObjectId.get())).block();
        service.add(new VillageEffectCommand.AddEffectCommand(village.getId(), EffectType.WOOD_BUFF, Duration.ofSeconds(2), 20)).block();
        Thread.sleep(4000);
        StepVerifier.create(service.remove(new VillageEffectCommand.RemoveInvalidEffectsCommand(village.getId())))
                .assertNext(next -> {
                    Assertions.assertThat(next.getEffects().size()).isEqualTo(0);
                })
                .verifyComplete();
    }
}