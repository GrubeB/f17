package pl.app.inventory.inventory.application.port.in;

import pl.app.inventory.inventory.application.domain.Inventory;
import reactor.core.publisher.Mono;

public interface InventoryService {
    Mono<Inventory> create(InventoryCommand.CreateInventoryCommand command);

    Mono<Inventory> add(InventoryCommand.AddItemCommand command);

    Mono<Inventory> remove(InventoryCommand.RemoveItemCommand command);

    Mono<Inventory> use(InventoryCommand.UseItemCommand command);
}
