package pl.app.inventory.inventory.application.port.in;

import org.bson.types.ObjectId;
import pl.app.inventory.inventory.application.domain.Inventory;
import reactor.core.publisher.Mono;

public interface InventoryDomainRepository {
    Mono<Inventory> fetchByPlayerId(ObjectId playerId);
}
