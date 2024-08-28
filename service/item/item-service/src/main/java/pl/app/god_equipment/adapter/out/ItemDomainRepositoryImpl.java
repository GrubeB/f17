package pl.app.god_equipment.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import pl.app.item.application.domain.Item;
import pl.app.item.query.ItemQueryService;
import pl.app.common.shared.model.ItemType;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ItemDomainRepositoryImpl implements
        pl.app.god_equipment.application.port.out.ItemDomainRepository,
        pl.app.trader.application.port.out.ItemDomainRepository
{
    private final ItemQueryService itemQueryService;

    @Override
    public Mono<Item> fetchById(ObjectId itemId) {
        return itemQueryService.fetchById(itemId);
    }
}
