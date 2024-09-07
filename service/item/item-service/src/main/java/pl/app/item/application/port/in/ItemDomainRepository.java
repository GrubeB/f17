package pl.app.item.application.port.in;

import org.bson.types.ObjectId;
import pl.app.common.shared.model.ItemType;
import pl.app.item.application.domain.Item;
import pl.app.item_template.application.domain.ItemTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ItemDomainRepository {
    Mono<Item> fetchById(ObjectId id);
}
