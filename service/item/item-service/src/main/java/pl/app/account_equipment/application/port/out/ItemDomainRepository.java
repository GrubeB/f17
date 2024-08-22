package pl.app.account_equipment.application.port.out;

import org.bson.types.ObjectId;
import pl.app.account_equipment.application.domain.AccountEquipment;
import pl.app.item.application.domain.Item;
import reactor.core.publisher.Mono;

public interface ItemDomainRepository {
    Mono<Item> fetchById(ObjectId itemId, String type);
}
