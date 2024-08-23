package pl.app.item.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pl.app.item.application.domain.Item;
import pl.app.item.application.domain.ItemException;
import pl.app.item.application.domain.Outfit;
import pl.app.item.application.domain.Weapon;
import pl.app.item_template.application.domain.ItemType;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ItemQueryServiceImpl implements ItemQueryService {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<? extends Item> fetchByIdAndType(@NonNull ObjectId id, ItemType type) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return switch (type) {
            case WEAPON -> mongoTemplate.query(Weapon.class).matching(query).one()
                    .switchIfEmpty(Mono.error(() -> ItemException.NotFoundItemException.fromId(id.toHexString())));
            case HELMET, ARMOR, GLOVES, BOOTS, BELT, RING, AMULET, TALISMAN ->
                    mongoTemplate.query(Outfit.class).matching(query).one()
                            .switchIfEmpty(Mono.error(() -> ItemException.NotFoundItemException.fromId(id.toHexString())));
            case POTION -> throw new RuntimeException("Not implemented yet");
        };
    }
}
