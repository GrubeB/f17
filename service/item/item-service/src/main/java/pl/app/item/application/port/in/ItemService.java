package pl.app.item.application.port.in;

import pl.app.item.application.domain.Item;
import pl.app.item_template.application.domain.ItemTemplate;
import pl.app.item_template.application.domain.OutfitTemplate;
import pl.app.item_template.application.domain.WeaponTemplate;
import pl.app.item_template.application.port.in.ItemTemplateCommand;
import reactor.core.publisher.Mono;

public interface ItemService {
    Mono<Item> createOutfit(ItemCommand.CreateOutfitCommand command);
    Mono<Item> createWeapon(ItemCommand.CreateWeaponCommand command);
}
