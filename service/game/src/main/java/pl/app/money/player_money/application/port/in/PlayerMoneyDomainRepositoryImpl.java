package pl.app.money.player_money.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.money.player_money.application.domain.PlayerMoney;
import pl.app.player.player.model.PlayerException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class PlayerMoneyDomainRepositoryImpl implements PlayerMoneyDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<PlayerMoney> fetchByPlayerId(ObjectId playerId) {
        return mongoTemplate.query(PlayerMoney.class)
                .matching(Query.query(Criteria.where("_id").is(playerId)))
                .one()
                .switchIfEmpty(Mono.error(() -> PlayerException.NotFoundPlayerException.fromId(playerId.toHexString())));
    }

    @Override
    public Flux<PlayerMoney> fetchAll() {
        return mongoTemplate.query(PlayerMoney.class)
                .all();
    }
}
