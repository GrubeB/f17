package pl.app.trader.adapter.out;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.app.common.shared.model.ItemType;
import pl.app.item.application.domain.Item;
import pl.app.item.application.port.in.ItemCommand;
import pl.app.item.application.port.in.ItemService;
import pl.app.trader.application.port.out.ItemGenerator;
import reactor.core.publisher.Flux;

import java.util.Set;

@Component
@RequiredArgsConstructor
class ItemGeneratorImpl implements ItemGenerator {
    private final ItemService itemService;

    @Override
    public Flux<Item> createRandomItems(Integer number, Set<ItemType> itemTypes, Integer level) {
        return itemService.createRandomItems(new ItemCommand.CreateRandomItemCommand(number, itemTypes, level));
    }

}
