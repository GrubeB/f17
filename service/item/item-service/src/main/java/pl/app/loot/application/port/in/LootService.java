package pl.app.loot.application.port.in;

import pl.app.loot.aplication.domain.Loot;
import pl.app.monster_gear.application.domain.MonsterGear;
import pl.app.monster_gear.application.port.in.MonsterGearCommand;
import reactor.core.publisher.Mono;

public interface LootService {
    Mono<Loot> crate(LootCommand.CreateLootCommand command);
    Mono<Loot> remove(LootCommand.RemoveLootCommand command);
    Mono<Loot> setItem(LootCommand.SetItemCommand command);
    Mono<Loot> removeItem(LootCommand.RemoveItemCommand command);
}
