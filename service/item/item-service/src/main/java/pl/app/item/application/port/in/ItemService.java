package pl.app.item.application.port.in;

import pl.app.item.application.domain.Item;
import reactor.core.publisher.Mono;

public interface ItemService {
    Mono<Item> createOutfit(ItemCommand.CreateOutfitCommand command);

    Mono<Item> createWeapon(ItemCommand.CreateWeaponCommand command);
}
