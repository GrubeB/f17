package pl.app.item.query;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.app.item.application.domain.Item;
import pl.app.item.query.dto.OutfitDto;
import pl.app.item_template.application.domain.ItemType;
import reactor.core.publisher.Mono;

public interface ItemQueryService {
    Mono<? extends Item> fetchByIdAndType(@NonNull ObjectId id, ItemType type);
}
