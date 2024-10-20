package pl.app.unit.village_army.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.unit.village_army.application.domain.VillageArmy;
import pl.app.unit.village_army.application.domain.VillageArmyException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class VillageArmyDomainRepositoryImpl implements VillageArmyDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<VillageArmy> fetchByVillageId(ObjectId villageId) {
        return mongoTemplate.query(VillageArmy.class)
                .matching(Query.query(Criteria.where("villageId").is(villageId)))
                .one()
                .switchIfEmpty(Mono.error(() -> VillageArmyException.NotFoundVillageArmyException.fromId(villageId.toHexString())));
    }

}
