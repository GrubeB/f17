package pl.app.account_equipment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import pl.app.account_equipment.application.port.out.ItemDomainRepository;
import pl.app.item.application.domain.Item;
import pl.app.item.query.ItemQueryService;
import pl.app.item_template.application.domain.ItemType;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ItemDomainRepositoryImpl implements ItemDomainRepository {
    private final ItemQueryService itemQueryService;

    @SuppressWarnings("unchecked")
    @Override
    public Mono<Item> fetchById(ObjectId itemId, String type) {
        return (Mono<Item>) itemQueryService.fetchByIdAndType(itemId, ItemType.valueOf(type));
    }
}
