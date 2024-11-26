package pl.app.gold_coin.gold_coin.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.gold_coin.gold_coin.application.domain.PlayerGoldCoin;
import pl.app.gold_coin.gold_coin.application.domain.PlayerGoldCoinException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class PlayerGoldCoinDomainRepositoryImpl implements PlayerGoldCoinDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<PlayerGoldCoin> fetchByPlayerId(ObjectId playerId) {
        return mongoTemplate.query(PlayerGoldCoin.class)
                .matching(Query.query(Criteria.where("_id").is(playerId)))
                .one()
                .switchIfEmpty(Mono.error(() -> PlayerGoldCoinException.NotFoundPlayerGoldCoinException.fromId(playerId.toHexString())));
    }

}
