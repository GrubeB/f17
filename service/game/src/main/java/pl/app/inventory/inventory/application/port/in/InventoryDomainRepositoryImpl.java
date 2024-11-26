package pl.app.inventory.inventory.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.inventory.inventory.application.domain.Inventory;
import pl.app.inventory.inventory.application.domain.InventoryException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class InventoryDomainRepositoryImpl implements InventoryDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Inventory> fetchByPlayerId(ObjectId playerId) {
        return mongoTemplate.query(Inventory.class)
                .matching(Query.query(Criteria.where("_id").is(playerId)))
                .one()
                .switchIfEmpty(Mono.error(() -> InventoryException.NotFoundInventoryException.fromId(playerId.toHexString())));
    }

}
