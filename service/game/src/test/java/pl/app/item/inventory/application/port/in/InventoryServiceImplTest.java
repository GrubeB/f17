package pl.app.item.inventory.application.port.in;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.app.item.item.application.domain.CrownPackItem;
import pl.app.player.player.application.domain.Player;
import pl.app.player.player.application.port.in.PlayerCommand;
import pl.app.player.player.application.port.in.PlayerService;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceImplTest {

    @Autowired
    private InventoryServiceImpl service;

    @Autowired
    private PlayerService playerService;


    @Test
    void add() {
        Player player = playerService.crate(new PlayerCommand.CreatePlayerCommand(ObjectId.get().toHexString(), "Ala")).block();

        StepVerifier.create(service.add(new InventoryCommand.AddItemCommand(player.getPlayerId(), new CrownPackItem(100), 1)))
                .assertNext(next -> {
                    Assertions.assertThat(next.getItem(new CrownPackItem(100)).isPresent()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void use_shouldUseItem() {
        Player player = playerService.crate(new PlayerCommand.CreatePlayerCommand(ObjectId.get().toHexString(), "Ala")).block();
        service.add(new InventoryCommand.AddItemCommand(player.getPlayerId(), new CrownPackItem(100), 1)).block();

        StepVerifier.create(service.use(new InventoryCommand.UseItemCommand(player.getPlayerId(), new CrownPackItem(100), 1, player.getPlayerId())))
                .assertNext(next -> {
                    Assertions.assertThat(next.getItem(new CrownPackItem(100)).isPresent()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void use_shouldUseOnlyOneItem() {
        Player player = playerService.crate(new PlayerCommand.CreatePlayerCommand(ObjectId.get().toHexString(), "Ala")).block();
        service.add(new InventoryCommand.AddItemCommand(player.getPlayerId(), new CrownPackItem(100), 2)).block();

        StepVerifier.create(service.use(new InventoryCommand.UseItemCommand(player.getPlayerId(), new CrownPackItem(100), 1, player.getPlayerId())))
                .assertNext(next -> {
                    Assertions.assertThat(next.getItem(new CrownPackItem(100)).get().getAmount()).isEqualTo(1);
                })
                .verifyComplete();
    }
}