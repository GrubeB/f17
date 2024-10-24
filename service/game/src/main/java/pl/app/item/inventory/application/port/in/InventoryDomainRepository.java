package pl.app.item.inventory.application.port.in;

import org.bson.types.ObjectId;
import pl.app.item.inventory.application.domain.Inventory;
import pl.app.money.player_money.application.domain.PlayerMoney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryDomainRepository {
    Mono<Inventory> fetchByPlayerId(ObjectId playerId);
}
