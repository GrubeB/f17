package pl.app.item.query;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import pl.app.item.application.domain.Item;
import pl.app.item_template.application.domain.ItemType;
import reactor.core.publisher.Mono;

public interface ItemQueryService {
    Mono<? extends Item> fetchByIdAndType(@NonNull ObjectId id, ItemType type);
}
