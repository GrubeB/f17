package pl.app.inventory.inventory.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.inventory.inventory.query.dto.InventoryDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryQueryService {
    Mono<InventoryDto> fetchByPlayerId(@NonNull ObjectId playerId);

    Flux<InventoryDto> fetchAll();
}
