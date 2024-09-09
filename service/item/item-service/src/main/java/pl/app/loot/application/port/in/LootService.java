package pl.app.loot.application.port.in;

import pl.app.loot.aplication.domain.Loot;
import reactor.core.publisher.Mono;

public interface LootService {
    Mono<Loot> crate(LootCommand.CreateLootCommand command);

    Mono<Loot> remove(LootCommand.RemoveLootCommand command);

    Mono<Loot> setItem(LootCommand.SetItemCommand command);

    Mono<Loot> removeItem(LootCommand.RemoveItemCommand command);
}
