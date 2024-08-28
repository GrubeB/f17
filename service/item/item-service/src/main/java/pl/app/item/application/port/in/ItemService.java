package pl.app.item.application.port.in;

import pl.app.item.application.domain.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ItemService {
    Mono<Item> createOutfit(ItemCommand.CreateOutfitCommand command);

    Mono<Item> createWeapon(ItemCommand.CreateWeaponCommand command);
    Flux<Item> createRandomItems(ItemCommand.CreateRandomItemCommand command);
}
