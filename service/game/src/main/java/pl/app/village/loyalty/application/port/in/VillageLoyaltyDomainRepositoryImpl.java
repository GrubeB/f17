package pl.app.village.loyalty.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.village.loyalty.application.domain.VillageLoyalty;
import pl.app.village.loyalty.application.domain.VillageLoyaltyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class VillageLoyaltyDomainRepositoryImpl implements VillageLoyaltyDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<VillageLoyalty> fetchById(ObjectId villageId) {
        return mongoTemplate.query(VillageLoyalty.class)
                .matching(Query.query(Criteria.where("_id").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> VillageLoyaltyException.NotFoundVillageLoyaltyException.fromId(villageId.toHexString())));
    }

    @Override
    public Flux<VillageLoyalty> fetchVillagesWithoutMaxLoyalty() {
        return mongoTemplate.query(VillageLoyalty.class)
                .matching(Query.query(Criteria.where("loyalty").lt(VillageLoyalty.LOYALTY_MAX)))
                .all();
    }
}
