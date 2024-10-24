package pl.app.item.inventory.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.item.inventory.application.domain.Inventory;
import pl.app.item.inventory.application.domain.InventoryException;
import pl.app.money.player_money.application.domain.PlayerMoney;
import pl.app.money.player_money.application.port.in.PlayerMoneyDomainRepository;
import pl.app.player.player.application.domain.PlayerException;
import reactor.core.publisher.Flux;
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
