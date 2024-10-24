package pl.app.item.inventory.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.item.inventory.query.dto.InventoryDto;
import pl.app.money.player_money.query.dto.PlayerMoneyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryQueryService {
    Mono<InventoryDto> fetchByPlayerId(@NonNull ObjectId playerId);

    Flux<InventoryDto> fetchAll();
}
