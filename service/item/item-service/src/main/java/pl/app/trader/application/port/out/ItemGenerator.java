package pl.app.trader.application.port.out;

import pl.app.common.shared.model.ItemType;
import pl.app.item.application.domain.Item;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface ItemGenerator {
    Flux<Item> createRandomItems(Integer number, Set<ItemType> itemTypes, Integer level);
}
