package pl.app.trader.adapter.out;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.trader.application.domain.Trader;
import pl.app.trader.application.domain.TraderException;
import pl.app.trader.application.port.out.TraderDomainRepository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class TraderDomainRepositoryImpl implements TraderDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Trader> fetchByGodId(ObjectId godId) {
        return mongoTemplate.query(Trader.class)
                .matching(Query.query(Criteria.where("godId").is(godId))).one()
                .switchIfEmpty(Mono.error(() -> TraderException.NotFoundTraderForGodException.fromGodId(godId.toString())));
    }
}
